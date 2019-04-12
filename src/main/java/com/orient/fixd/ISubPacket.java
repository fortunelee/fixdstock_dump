package com.orient.fixd;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

import com.orient.utils.GZIPUtils;

public abstract class ISubPacket {
	public abstract String subPacketToString();
	
	public abstract String msgType();
	
	public abstract boolean isJsonEncoded();
	
	public static String jsonGZIPBase64Encode(String jsonStr)
	{
		try {
			byte[] gzipBytes = GZIPUtils.compress(jsonStr);
			byte[] base64Bytes = Base64.encodeBase64(gzipBytes);
			for (int i = 0; i < base64Bytes.length; i++)
			{
				if (base64Bytes[i] == (byte)'=')
					base64Bytes[i] = (byte)'!';
			}
			return new String(base64Bytes, "UTF-8");
		} catch(IOException e) {
			e.printStackTrace();
			return "";
		} 
	}
	
	public static void main(String[] args) {
		
		String str = "3309afa";
		byte[] bytes;
		try {
			bytes = str.getBytes("UTF-8");
			byte[] encodedBytes = Base64.encodeBase64(bytes);
			System.out.println(new String(encodedBytes));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//return a json
	public static String jsonGZIPBase64Decode(String msg)
	{
		try
		{
			byte [] jsonBytes = msg.getBytes("UTF-8");
			if (jsonBytes == null || jsonBytes.length <= 0)
				return "";
			
			for (int i = 0; i < jsonBytes.length; i++)
			{
				if (jsonBytes[i] == (byte)'!')
					jsonBytes[i] = (byte)'=';
			}
			
			byte[] base64Bytes = Base64.decodeBase64(jsonBytes);
			if (base64Bytes == null || base64Bytes.length <= 0)
				return "";
			
			byte[] unGZIPBytes = GZIPUtils.uncompress(jsonBytes, 20 * 1024);
			if (unGZIPBytes == null)
				return "";
			
			return new String(unGZIPBytes, "UTF-8");
		} 
		catch(Exception e)
		{
			return "";
		}
	}
}
