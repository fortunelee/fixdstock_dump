package com.orient.fixd;

import com.alibaba.fastjson.annotation.JSONField;

public class LogonResponse extends ISubPacket {

	@JSONField(name="553")
	private String userName;
	
	@JSONField(name="108")
	private int heartBtInt;
	
	@JSONField(name="1130")
	private String serviceVer;
	
	public LogonResponse(String userName, int heartBtInt, String serviceVer)
	{
		this.userName = userName;
		this.heartBtInt = heartBtInt;
		this.serviceVer = serviceVer;
	}
	
	@Override
	public String subPacketToString() {
		StringBuilder b = new StringBuilder(512);
		b.append("553=").append(userName).append('|');
		b.append("108=").append(heartBtInt).append('|');
		b.append("1130=").append(serviceVer);
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

}
