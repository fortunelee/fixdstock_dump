package com.orient.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class EncryptUtil
{
	public static byte[] decryptPassword(String identity, byte[] encryptedPassword)
	{
		if (encryptedPassword == null)
			return null;
		
		byte[] first8 = UUIDUtils.getFirstBytes(identity, 8);
		byte[] last8r = UUIDUtils.getLastBytes(identity, 8, true);
		
		for (int i = encryptedPassword.length - 1; i >= 0; i--)
		{
			if (encryptedPassword[i] == (byte)'!')
				encryptedPassword[i] = '=';
			else
				break;
		}
		
		byte[] encryptedStrBase64 = Base64.decodeBase64(encryptedPassword);
		
		try {
			byte[] decryptedPassword = DesUtil.decrypt(encryptedStrBase64, first8, last8r);
			return decryptedPassword;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] encryptPassword(String identity, String password)
	{
		byte[] first8 = UUIDUtils.getFirstBytes(identity, 8);
		byte[] last8r = UUIDUtils.getLastBytes(identity, 8, true);
		
		byte[] encryptedPassword = null;
		try	{
			encryptedPassword = DesUtil.encrypt(password.getBytes("UTF-8"), first8, last8r);
		} catch(UnsupportedEncodingException e)	{
			e.printStackTrace();
		}
		
		byte[] base64Pwd = Base64.encodeBase64(encryptedPassword);
		
		for (int i = encryptedPassword.length - 1; i >= 0; i--)
		{
			if (encryptedPassword[i] == (byte)'!')
				encryptedPassword[i] = '=';
			else
				break;
		}
		
		return base64Pwd;
	}
}
