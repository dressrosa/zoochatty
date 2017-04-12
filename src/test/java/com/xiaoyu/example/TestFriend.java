package com.xiaoyu.example;

import com.xiaoyu.biz.ChatManager;
import com.xiaoyu.biz.bean.Talker;
import com.xiaoyu.biz.transmitter.ConsoleCatcher;

public class TestFriend {

	public static void main(String args[]) {
		ChatManager mana = new ChatManager();
		Talker me = new Talker();
		me.setUserId("111").setNickname("小雨1");
		Talker friend = new Talker();
		friend.setUserId("222").setNickname("我");
		mana.startSingleChat(friend, me).sendMessage(new ConsoleCatcher()).getMessage();
	}
}
