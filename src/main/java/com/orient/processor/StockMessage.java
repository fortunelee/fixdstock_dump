package com.orient.processor;

public class StockMessage {
	private int type = 0;
	
	public final static int SNAP = 1;
	public final static int ORDER = 2;
	public final static int TICK = 3;
	public final static int ORDERQUEUE = 4;
	
	public long microSeconds;  // 微秒

	public String securityID;
	
	public String msg;  // json格式

	public StockMessage(int type, long microSeconds, String securityID, String msg)
	{
		this.type = type;
		this.microSeconds = microSeconds;
		this.securityID = securityID;
		this.msg = msg;
	}
	
	public int getType()
	{
		return type;
	}
	
	public long getMicroSeconds()
	{
		return microSeconds;
	}
	
	public String securityID()
	{
		return securityID;
	}
	
	public String getMsg()
	{
		return msg;
	}
}
