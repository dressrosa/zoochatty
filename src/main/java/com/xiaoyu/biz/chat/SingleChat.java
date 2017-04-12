package com.xiaoyu.biz.chat;

import java.util.Date;

import com.xiaoyu.biz.bean.MessageBox;
import com.xiaoyu.biz.bean.Talker;
import com.xiaoyu.core.zk.ZooWrapper;

/**
 * 2017年4月12日下午5:05:33
 * 
 * @author xiaoyu
 * @description 单聊
 */
public class SingleChat implements Chatting {

	private Talker me;
	private Talker friend;
	private String mePath;
	private String friendPath;
	private ZooWrapper zoo;

	public SingleChat(Talker me, Talker friend) {
		this.me = me;
		this.friend = friend;
		initChat();
	}

	private void initChat() {
		mePath = "/" + me.getUserId();
		friendPath = "/" + friend.getUserId();
		zoo = new ZooWrapper(this);
		zoo.createPersistent(mePath, friendPath);
	}

	@Override
	public void endChat() {
		zoo.setOvered();
	}

	@Override
	public void sendMessage(String msg) {
		if ("".equals(msg) || msg == null)
			throw new NullPointerException("msg is null");
		if (msg.length() >= 100) {
			throw new IllegalArgumentException("msg length is too long(limit 150)");
		}
		MessageBox box = new MessageBox();
		box.setMsg(msg).setMarkId(new Date().getTime());

		// zoo.createEphemeral(friendPath + mePath);//临时节点无法实现离线消息
		// zoo.createPersistent(friendPath + mePath);
		zoo.writeData(friendPath + mePath, box);

		System.out.println(me.getNickname() + ":" + msg);
	}

	@Override
	public void getMessage() {
		zoo.subscribeDataChanges(mePath + friendPath);
	}

	@Override
	public void handleMessage(MessageBox box) {
		if (box != null && box.getMsg() != null) {
			System.out.println(friend.getNickname() + ":" + box.getMsg());
		}
	}

}
