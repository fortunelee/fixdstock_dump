package com.orient.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnapProcessor extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(SnapProcessor.class);
			
	public SnapProcessor() {
		super("SnapThread");
	}
	
//	public SnapProcessor(StockMessage sm)
//	{
//		if (sm.getType() != StockMessage.SNAP)
//			throw new RuntimeException(String.format("StockMessage.type = %d is error, should be = %d", sm.getType(), StockMessage.SNAP));
//	}
	
	private BlockingQueue<StockMessage> msgQueue = new LinkedBlockingQueue<StockMessage>();
	
	public boolean addSnapData(StockMessage sm)
	{
		return this.msgQueue.add(sm);
	}
	
	@Override
	public void run() {
		logger.info("SnapProcessorThread started.");
		
		while (true) {
			
		}
	}
}
