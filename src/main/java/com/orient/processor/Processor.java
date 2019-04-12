package com.orient.processor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Processor {
	private Processor() {}
	
	private static Processor inst = new Processor();
	public static Processor getInstance()
	{
		return inst;
	}
	
	public static String getDate()
	{
		Date currentTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String time = sdf.format(currentTime);
		return time;
	}
	
	private static TickProcessor tickProcessor = new TickProcessor();
	private static SnapProcessor snapProcessor = new SnapProcessor();
	private static OrderProcessor orderProcessor = new OrderProcessor();
	private static OrderQueueProcessor orderQueueProcessor = new OrderQueueProcessor();
	
	static {
		if (tickProcessor != null) {
			tickProcessor.start();
		}
		if (snapProcessor != null) {
			snapProcessor.start();
		}
		if (orderProcessor != null) {
			orderProcessor.start();
		}
		if (orderQueueProcessor != null) {
			orderQueueProcessor.start();
		}
	}
	
	public boolean ensureDataPathAndFiles(String pathPrefix, String market) throws InterruptedException
	{
		String path = String.format("%s/%s/%s", pathPrefix, getDate(), market);
		File dirPath = new File(path);
		while (!dirPath.exists()) {
			if (dirPath.mkdirs())
				break;
			
			Thread.sleep(10);
		}
		
		return true;
	}
	
	public boolean addStockMessage(StockMessage sm)
	{
		switch(sm.getType())
		{
		case StockMessage.SNAP:
			return addSnapMessage(sm);
		case StockMessage.ORDER:
			return addOrderMessage(sm);
		case StockMessage.TICK:
			return addTickMessage(sm);
		case StockMessage.ORDERQUEUE:
			return addOrderQueueMessage(sm);
		default:
			return false;
		}
	}
	
	public boolean addTickMessage(StockMessage sm)
	{
		return tickProcessor.addTickData(sm);
	}
	
	public boolean addSnapMessage(StockMessage sm)
	{
		return snapProcessor.addSnapData(sm);
	}
	
	public boolean addOrderMessage(StockMessage sm)
	{
		return orderProcessor.addOrderData(sm);
	}
	
	public boolean addOrderQueueMessage(StockMessage sm)
	{
		return orderQueueProcessor.addOrderQueueData(sm);
	}
	
}
