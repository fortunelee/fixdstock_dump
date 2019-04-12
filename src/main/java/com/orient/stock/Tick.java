package com.orient.stock;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;
import com.orient.processor.IDataMessage;
import com.orient.processor.StockMessage;

public class Tick implements IDataMessage {

	
	@JSONField(name="1500")
	private String mdStreamID;
	
	@JSONField(name="48")
	private String securityID;
	
	@JSONField(name="779")
	private String lastUpdateTime;
	
	@JSONField(name="44")
	private BigDecimal price;
	
	@JSONField(name="53")
	private long volume;
	
	@JSONField(name="1003")
	private String tradeNo;
	
	@JSONField(name="381")
	private BigDecimal amount;
	
	@JSONField(name="10179")
	private String buyNo;
	
	@JSONField(name="10180")
	private String sellNo;
	
	@JSONField(name="10192")
	private String bsFlag;

	public String getMdStreamID() {
		return mdStreamID;
	}

	public void setMdStreamID(String mdStreamID) {
		this.mdStreamID = mdStreamID;
	}

	public String getSecurityID() {
		return securityID;
	}

	public void setSecurityID(String securityID) {
		this.securityID = securityID;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getBuyNo() {
		return buyNo;
	}

	public void setBuyNo(String buyNo) {
		this.buyNo = buyNo;
	}

	public String getSellNo() {
		return sellNo;
	}

	public void setSellNo(String sellNo) {
		this.sellNo = sellNo;
	}

	public String getBsFlag() {
		return bsFlag;
	}

	public void setBsFlag(String bsFlag) {
		this.bsFlag = bsFlag;
	}
	
	@Override
	public String generateLine()
	{
		String line = String.format("%s,%s,%s,%s,%d,%s,%s,%s,%s,%s\n", 
				                    mdStreamID, securityID, lastUpdateTime, 
				                    price.toString(), volume, tradeNo, 
				                    amount.toString(), buyNo, sellNo, bsFlag);
		return line;
	}
	
	public static String generateHeader()
	{
		return "mdStreamID,securityID,lastUpdateTime,price,volume,tradeNo,amount,buyNo,sellNo,bsFlag\n";
	}
	
	public static int msgType()
	{
		return StockMessage.TICK;
	}
	
	public static String suffix()
	{
		return ".tick";
	}
	
	public static int columns()
	{
		return 10;
	}
	
}
