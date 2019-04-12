package com.orient.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderQueueProcessor extends Thread {
	
	private final static Logger logger = LoggerFactory.getLogger(OrderQueueProcessor.class);
	
	public OrderQueueProcessor() {
		super("OrderQueueThread");
	}
	
	private BlockingQueue<StockMessage> msgQueue = new LinkedBlockingQueue<StockMessage>();
	
	public boolean addOrderQueueData(StockMessage sm)
	{
		return this.msgQueue.add(sm);
	}
	
	@Override
	public void run() {
		logger.info("OrderQueueProcessorThread started.");
		super.run();
	}
}
