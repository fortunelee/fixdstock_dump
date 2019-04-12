package com.orient.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
	//private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss");
	public static String getNowTimeFormatted()
	{
		LocalDateTime ldt = LocalDateTime.now();
		return dtf.format(ldt);
	}
	
	public static String formatMicroSeconds(String originalTime, long microSeconds)
	{
		String updateTime = formatISODateString(originalTime);
		int microSecondsSuffix = (int)(microSeconds % 1000);
		return String.format("%s%03d", updateTime, microSecondsSuffix);
	}
	
	
	// this method needs java version >= java9
	public static int getNowMicroSeconds()
	{
		// 2019-04-09T16:44:33.962965
		int microSeconds = LocalDateTime.now().truncatedTo(ChronoUnit.MICROS).get(ChronoField.MICRO_OF_SECOND);  // 取6位数字
		return microSeconds;    // linux中准确(java9及以上版本), windows中不准确
	}
	
	private static String formatISODateString(String time)
	{
		String formatTime = "";
		int indexOfPlus = time.lastIndexOf('+');
		if (indexOfPlus > 0) {
			formatTime = time.substring(0, indexOfPlus);
		}
		
		formatTime = formatTime.replaceAll("T", " ");
		int indexOfPoint = time.lastIndexOf('.');
		
		if (indexOfPoint < 0)
			formatTime += ".000";
		else
		{
			int numberOfZeros = 23 - formatTime.length();
			if (numberOfZeros > 0) {
				StringBuffer buffer = new StringBuffer();
				for (int i = 0; i < numberOfZeros; i++)
					buffer.append('0');
				formatTime += buffer.toString();
			}
		}
		
		return formatTime;
	}
	
}
