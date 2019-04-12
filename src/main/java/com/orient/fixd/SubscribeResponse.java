package com.orient.fixd;

import com.alibaba.fastjson.annotation.JSONField;

public class SubscribeResponse extends ISubPacket {

	public SubscribeResponse(String requestID, String securityExchange)
	{
		this.requestID = requestID;
		this.securityExchange = securityExchange;
	}
	
	@JSONField(name="262")
	private String requestID = "";
	
	@JSONField(name="207")
	private String securityExchange;
	
	@Override
	public String subPacketToString() {
		StringBuilder b = new StringBuilder(256);
		if (requestID != null && !requestID.isEmpty())
			b.append("262=").append(requestID).append('|');
		b.append("207=").append(securityExchange);
		
		return b.toString();
	}

	@Override
	public String msgType() {
		return "V";
	}

	@Override
	public boolean isJsonEncoded() {
		return false;
	}

}
