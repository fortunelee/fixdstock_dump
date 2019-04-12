package com.orient.netty.client;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orient.common.ReadConfig;
import com.orient.processor.Processor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class NettyClient {
	private final static Logger logger = LoggerFactory.getLogger(NettyClient.class);
	
	private String host = "127.0.0.1";
	
	private int port = 5451;
	
	private String user = "";
	private String pwd = "";
	private String market = "";
	private String product = "";
	private String dataPath = "";
	private String pathSuffix = "";
	
	private ChannelFutureListener listener = null;
	
	private ReadConfig config = new ReadConfig();
	
	private HBTask hbTask = new HBTask();
	public HBTask getHBTask()
	{
		return hbTask;
	}
	
	public NettyClient(String pathFileName)
	{
		boolean ret = config.loadConfig(pathFileName);
		if (ret) {
			init();
		}
	}
	
	public boolean init()
	{
		String host = config.getConfigItem("server.host");
		if (!(host == null || host.isEmpty()))
			this.host = host;
		
		int port = config.getConfigItemInteger("server.port");
		if (port > 0)
			this.port = port;
			
		String user = config.getConfigItem("user");
		if (!(user == null || user.isEmpty()))
			this.user = user;
		
		String pwd = config.getConfigItem("pwd");
		if (!(pwd == null || pwd.isEmpty()))
			this.pwd = pwd;
		
		String market = config.getConfigItem("market");
		if (!(market == null || market.isEmpty()))
			this.market = market;
		
		String product = config.getConfigItem("product");
		if (!(product == null || product.isEmpty()))
			this.product = product;
		
		String dataPath = config.getConfigItem("datapath");
		if (!(dataPath == null || dataPath.isEmpty()))
			this.dataPath = dataPath;
		
		String pathSuffix = config.getConfigItem("pathsuffix");
		if (!(pathSuffix == null || pathSuffix.isEmpty()))
			this.pathSuffix = pathSuffix;
		
		// ensure data path.
		try {
			if (!Processor.getInstance().ensureDataPathAndFiles(this.dataPath, this.pathSuffix)) {
				logger.error("path mkdir failed.");
				System.exit(-1);
			}
		} catch(Exception e) {
			logger.error("path mkdir failed.");
			System.exit(-1);
		}
		
		hbTask.start();
		return true;
	}
	
	public void connect()
	{
		NettyClient thisclient = this;
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group)
		 .channel(NioSocketChannel.class)
		 .option(ChannelOption.TCP_NODELAY, true)
		 .handler(new ChannelInitializer<SocketChannel>() {

			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new LineBasedFrameDecoder(4 * 1024 * 1024, true, true));
				ch.pipeline().addLast(new ResponseHandler());
				ch.pipeline().addLast(new RequestHandler(user, pwd, market, product, thisclient));
			}
		});
		
		listener = new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture f) throws Exception {
				if (f.isSuccess()) {
                    logger.info("connect ok.");
                } else
				{
					logger.info("reconnecting...");
					f.channel().eventLoop().schedule(new Runnable() {
						@Override
						public void run() {
							connect();
						}
					}, 3, TimeUnit.SECONDS);
				}
			}
		};
		
		ChannelFuture future = b.connect(new InetSocketAddress(host, port));
		future.addListener(listener);
		
		try {
			future.channel().closeFuture().sync();
		} catch(InterruptedException e) {
			logger.error(e.getMessage());
		} 
		
		logger.info("finish from connect");
	}
}
