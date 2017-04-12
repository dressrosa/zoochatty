package com.xiaoyu.example;

import com.xiaoyu.biz.ChatManager;
import com.xiaoyu.biz.bean.Talker;
import com.xiaoyu.biz.transmitter.ConsoleCatcher;

public class TestMe {

	public static void main(String args[]) {
		ChatManager mana = new ChatManager();
		Talker me = new Talker();
		me.setUserId("111").setNickname("我");
		Talker friend = new Talker();
		friend.setUserId("222").setNickname("bob");
		mana.startSingleChat(me, friend).sendMessage(new ConsoleCatcher()).getMessage();
	}
}
