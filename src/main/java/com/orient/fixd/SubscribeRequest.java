package com.orient.fixd;

import com.alibaba.fastjson.annotation.JSONField;

public class SubscribeRequest extends ISubPacket {

	@JSONField(name="262")
	private String requestID = "";
	
	@JSONField(name="207")
	private String securityExchange;
	
	@JSONField(name="460")
	private String product;
	
	public SubscribeRequest(String requestID, String securityExchange, String product)
	{
		this.requestID = requestID;
		this.securityExchange = securityExchange;
		this.product = product;
	}
	
	public String getSecurityExchange()
	{
		return securityExchange;
	}
	
	public String getRequestID()
	{
		return requestID;
	}
	
	public String getProduct()
	{
		return product;
	}
	
	@Override
	public String subPacketToString() {
		StringBuilder b = new StringBuilder(512);
		if (requestID != null && !requestID.isEmpty())
			b.append("262=").append(requestID).append('|');
		
		b.append("207=").append(securityExchange).append('|');
		b.append("460=").append(product);
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
