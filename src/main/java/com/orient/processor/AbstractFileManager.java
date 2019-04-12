package com.orient.processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orient.common.ReadConfig;
import com.orient.constants.AppCommon;
import com.orient.stock.Order;
import com.orient.stock.OrderQueue;
import com.orient.stock.Snapshot;
import com.orient.stock.Tick;

public abstract class AbstractFileManager {
	/*
	 * 	public final static int SNAP = 1;
	 *  public final static int ORDER = 2;
	 *  public final static int TICK = 3;
	 *  public final static int ORDERQUEUE = 4;
	 */
	private int type = 0; // type-> StockMessage.type
	private String header = "";
	private String securityID = "";
	private int columns = 0;
	
	protected volatile boolean fileExists = false;
	
	protected ReadConfig config = new ReadConfig();
//	protected String pathPrefix = "";
	
	public AbstractFileManager(String securityID, String header, int columns, int type)
	{
		this.type = type;
		this.securityID = securityID;
		this.header = header;
		this.columns = columns;
		
		if (!config.loadConfig(AppCommon.CONFIG_PATH)) {
            throw new RuntimeException(String.format("load config:%s failed.", AppCommon.CONFIG_PATH));
        }
		
		String dataPath = config.getConfigItem("datapath"); 
		String pathSuffix = config.getConfigItem("pathsuffix");
		
		dirPath = String.format("%s/%s/%s", dataPath, Processor.getDate(), pathSuffix);
	}
	
	public String getHeader() {
		return header;
	}

	public String getSecurityID() {
		return securityID;
	}

	public int getColumns() {
		return columns;
	}

	public int getType() {
		return type;
	}
	
	public abstract boolean isFileExists();
	public boolean addSnap(Snapshot snap) { return false; }
	public boolean addTick(Tick tick) { return false; }
	public boolean addOrder(Order order) { return false; }
	public boolean addOrderQueue(OrderQueue orderqueue) { return false; }
	public abstract String getFileName();
	public abstract Writer getFileWriter();
	public abstract boolean flush();
	
	public String getDirPath()
	{
		return dirPath;
	}
	
	protected String fileName = "";
	protected String filePath = ""; 
	protected String dirPath = "";         // 子类不要改动
	
	protected BufferedWriter writer = null;
	
	public static class SnapFileManager extends AbstractFileManager
	{
		private final static Logger logger = LoggerFactory.getLogger(SnapFileManager.class);
		
		public final static String header = "xfadfafa";
		public final static int columns = 5; 
		public final static int type = StockMessage.SNAP;
		public SnapFileManager(String securityID)
		{
			super(securityID, header, columns, type);
			
			// set fileName
		}
		
		@Override
        public boolean addSnap(Snapshot snap)
		{
			// TODO
			return true;
		}

		@Override
		public String getFileName() {
			return fileName;
		}

		@Override
		public boolean flush() {
			try {
				writer.flush();
				return true;
			} catch (IOException e) {
				logger.error(e.getMessage());
				return false;
			}
		}

		@Override
		public Writer getFileWriter() {
			return writer;
		}

		@Override
		public boolean isFileExists() {
			return fileExists;
		}
		
		
	}
	
	public static class OrderFileManager extends AbstractFileManager
	{
		private final static Logger logger = LoggerFactory.getLogger(OrderFileManager.class);
		public final static String header = "aaa,bbb,ccc,ddd...";
		public final static int columns = 4;
		public final static int type = StockMessage.ORDER;
		public OrderFileManager(String securityID)
		{
			super(securityID, header, columns, type);
		}
		
		@Override
        public boolean addOrder(Order order)
		{
			// TODO
			return true;
		}

		@Override
		public String getFileName() {
			return fileName;
		}

		@Override
		public Writer getFileWriter() {
			return writer;
		}

		@Override
		public boolean flush() {
			try {
				writer.flush();
				return true;
			} catch (IOException e) {
				logger.error(e.getMessage());
				return false;
			}
		}

		@Override
		public boolean isFileExists() {
			return fileExists;
		}
	}
	
	public static class OrderQueueFileManager extends AbstractFileManager
	{
		private final static Logger logger = LoggerFactory.getLogger(OrderQueueFileManager.class);
		
		public final static String header = "mmm,nnn,lll,ppp...";
		public final static int columns = 4;
		public final static int type = StockMessage.ORDERQUEUE;
		public OrderQueueFileManager(String securityID)
		{
			super(securityID, header, columns, type);
		}
		
		@Override
        public boolean addOrderQueue(OrderQueue orderqueue)
		{
			// TODO 
			return true;
		}

		@Override
		public String getFileName() {
			return fileName;
		}

		@Override
		public Writer getFileWriter() {
			return writer;
		}

		@Override
		public boolean flush() {
			try {
				writer.flush();
				return true;
			} catch (IOException e) {
				logger.error(e.getMessage());
				return false;
			}
		}

		@Override
		public boolean isFileExists() {
			return fileExists;
		}
	}
	
	public static class TickFileManager extends AbstractFileManager
	{
		private final static Logger logger = LoggerFactory.getLogger(TickFileManager.class);
		
		public final static String header = Tick.generateHeader();
		public final static int columns = Tick.columns();
		public final static int type = Tick.msgType();
		public final static String fileSuffix = Tick.suffix();
		private File file = null;
		
		public Writer getWriter() 
		{
			return writer;
		}
		
		public TickFileManager(String securityID) throws IOException
		{
			super(securityID, header, columns, type);
			
			// set fileName
			fileName = securityID + fileSuffix;
			// set filePath
			filePath = getDirPath() + "/" + fileName;
			
			file = new File(filePath);
			fileExists = file.exists();
			if (!fileExists)
			{
				try {
					writer = new BufferedWriter(new FileWriter(file, true));
					fileExists = true;
					writer.append(header).flush();  // add header
				} catch(IOException e) {
					throw e;
				}
			}
			else
			{
				try {
					writer = new BufferedWriter(new FileWriter(file, true));
				} catch(IOException e) {
					throw e;
				}
			}
		}
		
		public boolean addTick(Tick tick, boolean flush)
		{
			try {
				if (writer != null && fileExists) {
					String line = tick.generateLine();
					writer.append(line);
					if (flush) {
                        writer.flush();
                    }
//					System.out.print("addTick finished, flush:" + flush + ": " + line);
					
					return true;
				}
				return false;
			} catch (IOException e) {
				return false;
			}
		}

		@Override
		public String getFileName() {
			return fileName;
		}

		@Override
		public Writer getFileWriter() {
			return writer;
		}

		@Override
		public boolean flush() {
			try {
				writer.flush();
				return true;
			} catch (IOException e) {
				logger.error(e.getMessage());
				return false;
			}
		}

		@Override
		public boolean isFileExists() {
			return fileExists;
		}
	}
	
//	public static void main(String[] args) throws IOException {
//		File file = new File("./data/2.txt");
//		BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
//		
//		writer.append("cygist\n").flush();
//		writer.close();
//	}
}
