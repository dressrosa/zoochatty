/**
 * 唯有读书,不慵不扰 
 **/
package com.xiaoyu.biz.chat;

import com.xiaoyu.biz.bean.MessageBox;

/**
 * 2017年4月12日下午5:04:59
 * 
 * @author xiaoyu
 * @description 具体的聊天
 */
public interface Chatting {

	public void endChat();

	public void sendMessage(String msg);

	public void getMessage();

	public void handleMessage(MessageBox msg);
}
