package com.orient.netty.client;

import java.io.IOException;

import com.orient.constants.AppCommon;

public class MainClass {
	
	public static void main(String[] args) throws IOException {
		
		NettyClient client = new NettyClient(AppCommon.CONFIG_PATH);
		
		client.connect();
		
		while (true)
		{
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
