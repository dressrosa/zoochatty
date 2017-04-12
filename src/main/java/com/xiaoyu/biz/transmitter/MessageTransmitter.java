/**
 * 唯有读书,不慵不扰 
 **/
package com.xiaoyu.biz.transmitter;

import com.xiaoyu.biz.chat.Chatting;

/**
 * 2017年4月12日下午5:07:07
 * 
 * @author xiaoyu
 * @description 消息发射器
 */
public interface MessageTransmitter {

	public String send(Chatting schat);
}
