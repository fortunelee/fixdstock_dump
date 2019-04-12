package com.orient.fixd;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.orient.utils.TimeUtils;
import com.orient.utils.UUIDUtils;

public class FixDPacket {
	
//	public final static int 
	@JSONField(serialize=false)
	private ISubPacket subPacket;
	
	@JSONField(name="8")
	private final static String beginString = "FixD.1.0.0";
	
	@JSONField(name="9")
	private int bodyLength = 0;
	
	@JSONField(name="35")
	private String msgType;
	
	@JSONField(name="52")
	private String sendingTime;
	
	@JSONField(serialize=false)
	private String subPacketStr;
	
	@JSONField(serialize=false)
	private boolean isSubPacketJsonEncoded;
	
	@JSONField(name="10")
	private String checkSum;
	
	@JSONField(serialize=false)
	private HashMap<Integer, String> keyValueMap = null;
	
//	@JSONField(serialize=false)
//	private String msgText = null;
	
	public FixDPacket(ISubPacket sub)
	{
		subPacket = sub;
	}
	
	public FixDPacket(String msg)
	{
		isSubPacketJsonEncoded = false;
		keyValueMap = new HashMap<>();
		String[] result = msg.split("|");
		for (String seg : result)
		{
			if (seg.isEmpty())
				continue;
			
			int equalIdx = seg.indexOf('=');
			String key = seg.substring(0, equalIdx);
			String value = seg.substring(equalIdx + 1);
			keyValueMap.put(Integer.parseInt(key), value);
		}
	}
	
	public ISubPacket getSubPacket()
	{
		return subPacket;
	}
	
	public String parseType()
	{
		if (keyValueMap != null)
		{
			String type = keyValueMap.get(35);
			if (type != null)
			{
				return type;
			}
		}
		
		return "";
	}
	
	public boolean checkCommonPart()
	{
		if (keyValueMap == null || keyValueMap.isEmpty())
			return false;
		
		String value = null;
		
		value = keyValueMap.get(8);
		if (value == null || !value.equals(beginString)) 
			return false;
		
		value = keyValueMap.get(9);
		if (value == null || Integer.parseInt(value) <= 0)
			return false;
		bodyLength = Integer.parseInt(value);
		
		value = keyValueMap.get(10);
		if (value == null)
			return false;
		checkSum = value;
		
		value = keyValueMap.get(52);
		if (value == null)
			return false;
		sendingTime = value;
		
		return true;
	}
	
	// identity鍦↙ogonRequest涓殑瑙ｆ瀽涓敤鍒�, 鍏朵綑鐨勭洿鎺ヨ祴绌哄��
	public boolean parse(String identity)
	{
		if (!checkCommonPart())
			return false;
		
		String value = null;
		value = keyValueMap.get(35);
		if (value == null)
			return false;
		if (value.equals("A"))
		{
			// LogonRequest
			String user = keyValueMap.get(553);
			String pwd  = keyValueMap.get(554);
			
			if (user != null && !user.isEmpty() && pwd != null && !pwd.isEmpty())
			{
				subPacket = new LogonRequest(user, pwd, identity);
				return true;
			}
		} 
		else if (value.equals("V"))
		{
			// SubscribeRequest
			String jsonEncoded = keyValueMap.get(355);
			
			if (jsonEncoded != null)
			{
				String json = ISubPacket.jsonGZIPBase64Decode(jsonEncoded);
				JSONObject jo = JSON.parseObject(json);
				if (jo != null && jo.containsKey("BOND"))
				{
					subPacket = new SubscribeRequest("", "BOND", "");
					return true;
				}
			}
			else
			{
				String reqID = keyValueMap.get(262);
				String exchange = keyValueMap.get(207);
				if (exchange.equals("BOND"))
				{
					subPacket = new SubscribeRequest(reqID, exchange, "");
					return true;
				}
			}
			
		}
		return false;
	}
	
	public String msgType()
	{
		return subPacket.msgType();
	}
	
	public String generate()
	{
		sendingTime = TimeUtils.getNowTimeFormatted();
		String msgType = subPacket.msgType();
		if (isSubPacketJsonEncoded)
		{
			if (msgType.equals("MD") || msgType.equals("TS"))
			{
				// MarketDepth or Transaction
				subPacketStr = subPacket.subPacketToString();
				StringBuilder b1 = new StringBuilder(2048);
				b1.append("35=").append(subPacket.msgType()).append('|');
				b1.append("52=").append(sendingTime).append('|');
				b1.append("354=").append(subPacketStr.length()).append('|');
				b1.append("355=").append(subPacketStr).append('|');
				
				String t0 = b1.toString();
				StringBuilder b0 = new StringBuilder(2048);
				b0.append("8=").append(beginString).append('|');
				b0.append("9=").append(t0.length()).append('|').append(t0);
				
				String t1 = b0.toString();
				try {
					byte[] t1_bytes = t1.getBytes("UTF-8");
					checkSum = UUIDUtils.moduloCheckSum(t1_bytes);
					return t1 + "10=" + checkSum + "|\n";
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
			}
			
			return "";
		}
		else
		{
			if (msgType.equals("WELCOME"))
			{
				// subpacket
				subPacketStr = subPacket.subPacketToString();
				StringBuilder b1 = new StringBuilder(1024);
				b1.append("35=").append(subPacket.msgType()).append('|');
				b1.append("52=").append(sendingTime).append('|');
				b1.append(subPacketStr).append('|');
				
				String t0 = b1.toString();
				StringBuilder b0 = new StringBuilder(1024);
				b0.append("8=").append(beginString).append('|');
				b0.append("9=").append(t0.length()).append('|').append(t0);
				
				String t1 = b0.toString();
				try {
					byte[] t1_bytes = t1.getBytes("UTF-8");
                    checkSum = UUIDUtils.moduloCheckSum(t1_bytes);
					return t1 + "10=" + checkSum + "|\n";
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
			}
			else if (msgType.equals("A"))
			{
				// LogonResponse
				subPacketStr = subPacket.subPacketToString();
				StringBuilder b1 = new StringBuilder(1024);
				b1.append("35=").append(subPacket.msgType()).append('|');
				b1.append("52=").append(sendingTime).append('|');
				b1.append(subPacketStr).append('|');
				
				String t0 = b1.toString();
				StringBuilder b0 = new StringBuilder(1024);
				b0.append("8=").append(beginString).append('|');
				b0.append("9=").append(t0.length()).append('|').append(t0);
				
				String t1 = b0.toString();
				try {
					byte[] t1_bytes = t1.getBytes("UTF-8");
					checkSum = UUIDUtils.moduloCheckSum(t1_bytes);
					return t1 + "10=" + checkSum + "|\n";
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
			}
			else if (msgType.equals("V"))
			{
				// SubscribeResponse
				subPacketStr = subPacket.subPacketToString();
				StringBuilder b1 = new StringBuilder(1024);
				b1.append("35=").append(subPacket.msgType()).append('|');
				b1.append("52=").append(sendingTime).append('|');
				b1.append(subPacketStr).append('|');
				
				String t0 = b1.toString();
				StringBuilder b0 = new StringBuilder(1024);
				b0.append("8=").append(beginString).append('|');
				b0.append("9=").append(t0.length()).append('|').append(t0);
				
				String t1 = b0.toString();
				try {
					byte[] t1_bytes = t1.getBytes("UTF-8");
					checkSum = UUIDUtils.moduloCheckSum(t1_bytes);
					return t1 + "10=" + checkSum + "|\n";
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
			} 
			else if (msgType.equals("0"))
			{
				// HeartbeatResponse
				subPacketStr = subPacket.subPacketToString();
				StringBuilder b0 = new StringBuilder(1024);
				b0.append("8=").append(beginString).append('|');
				b0.append("9=").append(0).append('|');
				b0.append("35=").append(subPacket.msgType()).append('|');
				b0.append("52=").append(sendingTime).append('|');
				
				String t1 = b0.toString();
				try {
					byte[] t1_bytes = t1.getBytes("UTF-8");
					checkSum = UUIDUtils.moduloCheckSum(t1_bytes);
					return t1 + "10=" + checkSum + "|\n"; 
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					return null;
				}
			}
			
			return "";
		}
	}
	
	
//	public static void main(String[] args) {
//		String str = "|35=A|52=20180829-10:36:29|553=testuser|554=asM3Uxg9JXaBAj533p61xQ!!|";
//		System.out.println(str.length());
//	}
	/////////////////////////////////////////////////////////////////////////////
	//public static void main(String[] args) {
		//FixDPacket fixd = new FixDPacket("8=FixD.1.0.0|9=70|35=A|52=2018-03-16 13:26:40|553=helloworld|554=fyjaIKf+rcgROWTJydlqqA!!|10=016|");
//		String uuid = UUIDUtils.generateUUID();
//		FixDPacket fixd = new FixDPacket(new WelcomeResponse(uuid));
//		
//		System.out.println(fixd.generate());
//		
//		LogonResponse lr = new LogonResponse("useruser", 5, "1.0");
//		FixDPacket fixd2 = new FixDPacket(lr);
//		System.out.println(fixd2.generate());
//		
//		SubscribeResponse sr = new SubscribeResponse("xxx", "BOND");
//		FixDPacket fixd3 = new FixDPacket(sr);
//		System.out.println(fixd3.generate());
//		
//		SubscribeResponse sr2 = new SubscribeResponse("", "BOND");
//		FixDPacket fixd4 = new FixDPacket(sr2);
//		System.out.println(fixd4.generate());
	//}
}
