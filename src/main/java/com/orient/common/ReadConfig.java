package com.orient.common;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ReadConfig {
	private Properties config = new Properties();
	
	public boolean loadConfig(String fileName)
	{
		File file = new File(fileName);
		if (file.exists() && file.canRead())
		{
			try
			{
				config.load(new FileReader(file));
				return true;
			} catch(IOException ex) 
			{
				System.out.println(ex.getMessage());
				return false;
			}
		}
		return false;
	}
	
	public String getConfigItem(String keyItem)
	{
		if (keyItem.isEmpty())
			return "";
		
		return config.getProperty(keyItem);
	}
	
	public int getConfigItemInteger(String keyItem)
	{
		String value = getConfigItem(keyItem);
		if (value == null || value.isEmpty())
			return 0;
		
		try
		{
			Integer a = Integer.parseInt(value);
			return a.intValue();
		} 
		catch(NumberFormatException ex)
		{
			return 0;
		}
	}
}
