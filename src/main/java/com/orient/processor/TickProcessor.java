package com.orient.processor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.orient.processor.AbstractFileManager.TickFileManager;
import com.orient.stock.Tick;
import com.orient.utils.TimeUtils;

public class TickProcessor extends Thread {
	private final static Logger logger = LoggerFactory.getLogger(TickProcessor.class);
	
	public TickProcessor() {
		super("TickThread");
	}
	
//	public static void main(String[] args) throws InterruptedException, IOException {
//		
//		String msg = " [{\"1003\":\"1334655\",\"381\":22792.0,\"10179\":\"2204433\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.49,\"53\":800,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334656\",\"381\":509792.0,\"10179\":\"2202652\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.48,\"53\":17900,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334657\",\"381\":2848.0,\"10179\":\"2203557\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.48,\"53\":100,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334658\",\"381\":2848.0,\"10179\":\"2204008\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.48,\"53\":100,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334659\",\"381\":34176.0,\"10179\":\"2204119\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.48,\"53\":1200,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334660\",\"381\":54112.0,\"10179\":\"2204219\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.48,\"53\":1900,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334661\",\"381\":8544.0,\"10179\":\"2204632\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.48,\"53\":300,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334662\",\"381\":512460.0,\"10179\":\"1998450\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":18000,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334663\",\"381\":14235.0,\"10179\":\"2023457\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":500,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334664\",\"381\":8541.0,\"10179\":\"2067738\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":300,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334665\",\"381\":28470.0,\"10179\":\"2075125\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":1000,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334666\",\"381\":2847.0,\"10179\":\"2080346\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":100,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334667\",\"381\":28470.0,\"10179\":\"2102610\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":1000,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334668\",\"381\":2847.0,\"10179\":\"2143335\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":100,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334669\",\"381\":14235.0,\"10179\":\"2179416\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":500,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334670\",\"381\":2847.0,\"10179\":\"2200429\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":100,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"},{\"1003\":\"1334671\",\"381\":14235.0,\"10179\":\"2202751\",\"10180\":\"2204733\",\"10192\":\"S\",\"10115\":\"\",\"44\":28.47,\"53\":500,\"1500\":\"TI\",\"48\":\"600446\",\"779\":\"2019-04-08T10:06:00.07+08:00\"}]";
//		String msg2 = "[{\"1003\":\"1335977\",\"381\":14410.0,\"10179\":\"2203159\",\"10180\":\"2201630\",\"10192\":\"B\",\"10115\":\"\",\"44\":14.41,\"53\":1000,\"1500\":\"TI\",\"48\":\"600584\",\"779\":\"2019-04-08T10:05:57.86+08:00\"}]";
//		TickProcessor tickProcessor = new TickProcessor();
//		tickProcessor.start();
//		Thread.sleep(1000);
//		//int type, long microSeconds, String securityID, String msg
//		StockMessage sm = new StockMessage(StockMessage.TICK, TimeUtils.getNowMicroSeconds(), "600446", msg2);
//		tickProcessor.addTickData(sm);
//		tickProcessor.addTickData(sm);
//		logger.info("add tick finished.");
//		
//		Thread.sleep(10000);
//	}

	
	private BlockingQueue<StockMessage> msgQueue = new LinkedBlockingQueue<StockMessage>();
	
	public boolean addTickData(StockMessage sm)
	{
		return this.msgQueue.add(sm);
	}
	
	private boolean process(StockMessage sm)
	{
		if (sm.getType() != StockMessage.TICK)
			return false;
		
		String msg = sm.getMsg();
		
		List<Tick> ticks = JSON.parseArray(msg, Tick.class);
		long microSeconds = sm.getMicroSeconds();
		
		doProcessTick(ticks, microSeconds);
		return true;
	}
	
	private HashMap<String, TickFileManager> fileMap = new HashMap<String, TickFileManager>();
	
	private boolean doProcessTick(List<Tick> ticks, long microSeconds)
	{
		TickFileManager fileManager = null;
		
		String securityID = ticks.get(0).getSecurityID();
		if (fileMap.containsKey(securityID))
		{
			fileManager = fileMap.get(securityID);
		}
		else
		{
			try {
				fileManager = new TickFileManager(securityID);
				fileMap.put(securityID, fileManager);
			} catch (IOException e) {
				return false;
			}
		}
		
		boolean ret = false;
		for (Tick tick : ticks) {
			tick.setLastUpdateTime(TimeUtils.formatMicroSeconds(tick.getLastUpdateTime(), microSeconds));
			ret = ret && fileManager.addTick(tick, false);
		}
		
		ret = ret && fileManager.flush();
		return ret;
	}
	
	@Override
	public void run() {
		logger.info("TickProcessorThread started.");
		
		while (true) {
			StockMessage sm = null;
			try {
				sm = msgQueue.poll(1, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (sm == null)
				continue;
			
			process(sm);
		}
	}
}
