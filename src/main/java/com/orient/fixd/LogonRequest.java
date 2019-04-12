package com.orient.fixd;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.alibaba.fastjson.annotation.JSONField;
import com.orient.utils.EncryptUtil;

public class LogonRequest extends ISubPacket {

	@JSONField(name="553")
	private String userName;
	
	@JSONField(name="554")
	private String password; // 最好不用string类型存password
	
	@JSONField(serialize=false)
	private String identity = null;
	
	public LogonRequest(String username, String password, String identity)
	{
		this.userName = username;
		this.password = password;
		this.identity = identity;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getUser()
	{
		return userName;
	}
	
	public String getIdentity()
	{
		return identity;
	}
	
	public void setIdentity(String identity)
	{
		this.identity = identity;
	}
	
	public static boolean authPassword(String identity, String user, String password)
	{
		// it it temporary.
		if (user.equals(password))
			return true;
		return false;
	}
	
	@Override
	public String subPacketToString() {
		StringBuilder b = new StringBuilder(512);
		b.append("553=").append(userName).append('|');
		b.append("554=").append(password);
		return b.toString();
	}

	@Override
	public String msgType() {
		return "A";
	}

	@Override
	public boolean isJsonEncoded() {
		return false;
	}
	
	//System.out.println(lr.subPacketToString());
	//boolean ret = authPassword(id, lr.getPassword());
	//System.out.println(ret);
	//System.out.println(id.length());
	
	public static void test() throws UnsupportedEncodingException
	{
		String id = "1c984faa49284044afef5e115f927789";
		String str = "helloworld";
		LogonRequest lr = new LogonRequest(str, str, id);
		
		byte[] encryptedPwd = EncryptUtil.encryptPassword(id, str);
		
		System.out.println(new String(encryptedPwd));
		byte[] decryptedPwd = EncryptUtil.decryptPassword(id, encryptedPwd);
		System.out.println(new String(decryptedPwd));
		if (Arrays.equals(str.getBytes("UTF-8"), decryptedPwd))
		{
			System.out.println(true);
		}
	}
	
//	public static void main(String[] args) throws UnsupportedEncodingException 
//	{
//		LogonRequest lr  = new LogonRequest();
//		test();
//	}

}
