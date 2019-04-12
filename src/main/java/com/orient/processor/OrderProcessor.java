package com.orient.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderProcessor extends Thread {
	
	private final static Logger logger = LoggerFactory.getLogger(OrderProcessor.class);
	
	public OrderProcessor() {
		super("OrderThread");
	}
	
	private BlockingQueue<StockMessage> msgQueue = new LinkedBlockingQueue<StockMessage>();
	
	public boolean addOrderData(StockMessage sm)
	{
		return this.msgQueue.add(sm);
	}
	
	@Override
	public void run() {
		logger.info("OrderProcessorThread started.");
		
		while (true)
		{
			
		}
	}
}
