package com.orient.utils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.UUID;

public class UUIDUtils {
	
	public static String generateUUID()
	{
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static byte[] getFirstBytes(String str, int n)
	{
		byte[] str_bytes = null;
		try {
			str_bytes = str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		if (str_bytes == null)
			return null;
		
		if (n >= str_bytes.length)
			return str_bytes;
		else if (n <= 0)
			return null;
		
		byte[] ret = new byte[n];
		System.arraycopy(str_bytes, 0, ret, 0, n);
		return ret;
	}
	
	public static byte[] getLastBytes(String str, int n, boolean reverse)
	{
		byte[] str_bytes = null;
		
		try	{
			str_bytes = str.getBytes("UTF-8");
		} catch(UnsupportedEncodingException e)	{
			e.printStackTrace();
		}
		
		if (str_bytes == null)
			return null;
	
		if (n >= str_bytes.length)
			return str_bytes;
		else if (n <= 0)
			return null;
		
		byte[] ret = new byte[n];
		int srcPos = str_bytes.length - n;
		System.arraycopy(str_bytes, srcPos, ret, 0, n);
		
		if (reverse)
			reverseBytes(ret);
		
		return ret;
	}
	
	public static void reverseBytes(byte[] bytes)
	{
		int len = bytes.length;
		
		int times = len / 2;
		for (int i = 0; i < times; i++)
		{
			byte temp = bytes[i];
			bytes[i] = bytes[len - 1 - i];
			bytes[len - 1 - i] = temp;
		}
	}
	
	private static DecimalFormat format = new DecimalFormat("000");
	public static String moduloCheckSum(byte[] bytes)
	{
		
		long sum = 0;
		for (int i = 0; i < bytes.length; i++)
		{
			sum += bytes[i];
		}
		
		//return String.format("%0d", (short)(sum % 256));
		
		return format.format((short)(sum % 256));
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////Test////////////////////////////////////////
//	private static void test1()
//	{
//		for (int i = 0; i < 10; i++) {
//			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
//			
//			System.out.println(uuid + ", len = " + uuid.length());
//		}
//	}
//	public static void main(String[] args) throws UnsupportedEncodingException {
//		test1();
//		
//		String a = "1234567890abcdef";
//		byte[] ret = getFirstBytes(a, 8);
//		System.out.println(new String(ret, "UTF-8"));
//		
//		int n = 8;
//		ret = getLastBytes(a, n, false);
//		System.out.println(new String(ret, "UTF-8"));
//		
//		ret = getLastBytes(a, n, true);
//		System.out.println(new String(ret, "UTF-8"));
//	}
	/////////////////////////////////////////////////////////////////////////////
}
