package com.orient.utils;

import java.util.List;

public class StringUtils {
	public static String join(String[] str_arr, String delim)
	{
		StringBuilder builder = new StringBuilder(1024);
		int len = str_arr.length;
		for (int i = 0; i < len - 1; i++)
		{
			if (!str_arr[i].isEmpty())
				builder.append(str_arr[i]).append(delim);
		}
		
		builder.append(str_arr[len - 1]);
		return builder.toString();
	}
	
	public static String join(List<String> str_arr, String delim)
	{
		StringBuilder builder = new StringBuilder(1024);
		int len = str_arr.size();
		for (int i = 0; i < len - 1; i++)
		{
			if (!str_arr.get(i).isEmpty())
				builder.append(str_arr.get(i)).append(delim);
		}
		
		builder.append(str_arr.get(len - 1));
		return builder.toString();
	}
	
}
