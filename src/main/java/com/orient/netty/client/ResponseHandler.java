package com.orient.netty.client;

import com.orient.fixd.FixDPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseHandler extends MessageToByteEncoder<FixDPacket> {

	@Override
	protected void encode(ChannelHandlerContext ctx, FixDPacket msg, ByteBuf out) throws Exception {
		String msgStr = msg.generate();
		
		//System.out.println("send msg:" + msgStr);
		out.writeBytes(msgStr.getBytes("UTF-8"));
	}

}
