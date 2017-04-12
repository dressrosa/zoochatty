/**
 * 唯有读书,不慵不扰 
 **/
package com.xiaoyu.biz.transmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Random;

import com.xiaoyu.biz.chat.Chatting;

/**
 * 2017年4月12日下午5:07:25
 * 
 * @author xiaoyu
 * @description 控制台发送
 */
public class ConsoleCatcher implements MessageTransmitter {

	@Override
	public String send(final Chatting chat) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("打开聊天窗口,等待输入:");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, Charset.forName("utf-8")));
				String str = null;
				try {
					while ((str = reader.readLine()) != null && str.length() != 0) {
						chat.sendMessage(str);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, "t-send-" + new Random().nextInt(100)).start();
		return null;
	}

}
