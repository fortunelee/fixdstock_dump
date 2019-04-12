package com.orient.netty.client;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orient.fixd.FixDPacket;
import com.orient.fixd.LogonRequest;
import com.orient.fixd.SubscribeRequest;
import com.orient.processor.Processor;
import com.orient.processor.StockMessage;
import com.orient.utils.EncryptUtil;
import com.orient.utils.GZIPUtils;
import com.orient.utils.TimeUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RequestHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private final static Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	
	private String user;
	private String pwd;
	private String market;
	private String product;
	
	public final static String MARKET_SH = "TDF_SHL2";
	public final static String MARKET_SZ = "TDF_SZL2";
	public final static String ALL_PRODUCT = "I,W,TI,SO,OQ";
	
	private NettyClient client = null;
	
	public RequestHandler(String user, String pwd, String market, String product, NettyClient client)
	{
		this.user = user;
		this.pwd = pwd;
		this.market = market;
		this.product = product;
		this.client = client;
		
		if (market == null || market.isEmpty())
			this.market = MARKET_SH;
		
		if (product == null || product.isEmpty())
			this.product = ALL_PRODUCT;
	}
	
	private boolean processPacket(String msg, HashMap<Integer, String> outMap)
	{
		String [] sections = msg.split("\\|");
		for (int i = 0; i < sections.length; i++)
		{
			int idx = sections[i].indexOf("=");
			if (idx < 0)
				continue;
			
			String key = sections[i].substring(0, idx);
			String value = sections[i].substring(idx + 1);
			outMap.put(Integer.parseInt(key), value);
		}
		return true;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		int len = buf.readableBytes();
		byte[] bytes = new byte[len];
		buf.readBytes(bytes);
		
		String msg = new String(bytes, "UTF-8");
		
		HashMap<Integer, String> sections = new HashMap<>();
		if (processPacket(msg, sections))
		{
			String msgType = sections.get(35);
			if (msgType.equals("WELCOME"))
			{
				onWelcome(ctx, msg, sections);
			}
			else if (msgType.equals("W"))
			{
				// snapshot
				onSnapshot(msg, sections);
			}
			else if (msgType.equals("OQ"))
			{
				// order_queue
				onOrderQueue(msg, sections);
			}
			else if (msgType.equals("SO"))
			{
				// single_order
				onSingleOrder(msg, sections);
			}
			else if (msgType.equals("TI"))
			{
				// tick_data
				onTickData(msg, sections);
			}
			else if (msgType.equals("A"))
			{
				onLogonReturn(ctx, msg, sections);
			}
			else if (msgType.equals("0"))
			{
				// heartbeat
				OnHeartBeat(ctx, msg, sections);
			}
			else if (msgType.equals("V"))
			{
				// subscribe
				logger.info("subscribe:" + msg);
			}
			else
			{
				logger.info("unknown:" + msg);
			}
		}
	}
	
	private void onWelcome(ChannelHandlerContext ctx, String msg, HashMap<Integer, String> sections)
	{
		String identity = sections.get(1400);
		byte[] encryptedPwd = EncryptUtil.encryptPassword(identity, pwd);
		
		try {
			LogonRequest lr = new LogonRequest(user, new String(encryptedPwd, "UTF-8"), identity);
			FixDPacket fixd = new FixDPacket(lr);
			ctx.writeAndFlush(fixd);
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) throws UnsupportedEncodingException {
//		String identity = "9f06d3a31a984c5bb2f9cbea3754f6a3";
//		String passwd = "testuser";
//		
//		byte[] encryptedPwd = EncryptUtil.encryptPassword(identity, passwd);
//		String encrypt_pass = new String(encryptedPwd, "UTF-8");
//		System.out.println(encrypt_pass);
//	}
	
	
	private void OnHeartBeat(ChannelHandlerContext ctx, String msg, HashMap<Integer, String> sections)
	{
		logger.info("hb recved:" + msg);
		
		HBTask hbTask = client.getHBTask();
		hbTask.setHBRecved();
	}
	
	
	private void onLogonReturn(ChannelHandlerContext ctx, String msg, HashMap<Integer, String> sections)
	{
		logger.info("logon recved:" + msg);
		SubscribeRequest sr = new SubscribeRequest("", MARKET_SH, product);
		FixDPacket fixd = new FixDPacket(sr);
		ctx.writeAndFlush(fixd);
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("connected to server:" + ctx.channel().remoteAddress().toString());
		
		HBTask hbTask = client.getHBTask();
		hbTask.setChannel(ctx.channel());
		hbTask.setHBRecved();
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("disconnected to server:" + ctx.channel().remoteAddress().toString());
		Thread.sleep(3 * 1000);
		
		HBTask hbTask = client.getHBTask();
		hbTask.setChannel(null);
		
		if (client != null)
			client.connect();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("exception caught:" + cause.getMessage());
		
		HBTask hbTask = client.getHBTask();
		hbTask.setChannel(null);
	}
	
	private void changeBase64String(byte[] jsonBytes)
	{
		for (int i = jsonBytes.length - 1; i >= 0; i--)
		{
			if (jsonBytes[i] == (byte)'!')
				jsonBytes[i] = (byte)'=';
			else
				break;
		}
	}
	
	private String decode(String jsonText)
	{
		if (jsonText == null)
			return null;
		
		byte[] jsonBytes = null;
		try {
			jsonBytes = jsonText.getBytes("UTF-8");
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
		changeBase64String(jsonBytes);
		
		byte[] base64Bytes = Base64.decodeBase64(jsonBytes);
		byte[] uncompressBytes = GZIPUtils.uncompress(base64Bytes, 4 * 1024 * 1024);
		
		try {
			String jsonDecoded = new String(uncompressBytes, "UTF-8");
			return jsonDecoded;
		} catch(UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void onSnapshot(String msg, HashMap<Integer, String> sections)
	{
		String mkt = sections.get(207);
		if (this.market.equals(mkt))
		{
			String jsonDecoded = decode(sections.get(355));
			logger.info("snap: " + jsonDecoded);
			
			String securityID = sections.get(48);
			StockMessage sm = new StockMessage(StockMessage.SNAP, TimeUtils.getNowMicroSeconds(), securityID, jsonDecoded);
			Processor.getInstance().addSnapMessage(sm);
		}
	}
	
	private void onOrderQueue(String msg, HashMap<Integer, String> sections)
	{
		String mkt = sections.get(207);
		if (this.market.equals(mkt))
		{
			String jsonDecoded = decode(sections.get(355));
			logger.info("orderqueue: " + jsonDecoded);
			
			String securityID = sections.get(48);
			StockMessage sm = new StockMessage(StockMessage.ORDERQUEUE, TimeUtils.getNowMicroSeconds(), securityID, jsonDecoded);
			Processor.getInstance().addOrderQueueMessage(sm);
		}
	}
	
	private void onSingleOrder(String msg, HashMap<Integer, String> sections)
	{
		String mkt = sections.get(207);
		if (this.market.equals(mkt))
		{
			String jsonDecoded = decode(sections.get(355));
			logger.info("singleorder: " + jsonDecoded);
			
			String securityID = sections.get(48);
			StockMessage sm = new StockMessage(StockMessage.ORDER, TimeUtils.getNowMicroSeconds(), securityID, jsonDecoded);
			Processor.getInstance().addOrderMessage(sm);
		}
	}
	
	private void onTickData(String msg, HashMap<Integer, String> sections)
	{
		String mkt = sections.get(207);
		if (this.market.equals(mkt))
		{
			String jsonDecoded = decode(sections.get(355));
			logger.info("tickdata: " + jsonDecoded);
			
			String securityID = sections.get(48);
			StockMessage sm = new StockMessage(StockMessage.TICK, TimeUtils.getNowMicroSeconds(), securityID, jsonDecoded);
			Processor.getInstance().addTickMessage(sm);
		}
	}
	
}
