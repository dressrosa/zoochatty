/**
 * 唯有读书,不慵不扰 
 **/
package com.xiaoyu.biz;

import java.util.Date;

import com.xiaoyu.biz.bean.Talker;
import com.xiaoyu.biz.chat.Chatting;
import com.xiaoyu.biz.chat.SingleChat;
import com.xiaoyu.biz.transmitter.MessageTransmitter;

/**
 * 2017年4月12日下午5:08:15
 * 
 * @author xiaoyu
 * @description 聊天管理器,来实现不同的聊天方式
 */
public class ChatManager {

	private Chatting chat;

	public ChatManager startSingleChat(Talker me, Talker friend) {
		chat = new SingleChat(me, friend);
		return this;
	}

	public ChatManager sendMessage(MessageTransmitter transmitter) {
		if (chat == null)
			throw new IllegalArgumentException("please start chatting first");
		if (transmitter == null)
			chat.sendMessage(new Date().toString());
		transmitter.send(chat);
		return this;
	}

	public ChatManager getMessage() {
		chat.getMessage();
		return this;
	}

	public void endChat() {
		chat.endChat();
	}

}
