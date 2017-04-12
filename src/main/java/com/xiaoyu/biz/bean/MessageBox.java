/**
 * 唯有读书,不慵不扰 
 **/
package com.xiaoyu.biz.bean;

import java.io.Serializable;

/**
 * 2017年4月12日下午4:56:21
 * 
 * @author xiaoyu
 * @description 封装消息
 */
public class MessageBox implements Serializable {

	private static final long serialVersionUID = 1L;

	private String msg;// 消息
	private Long markId;// 消息的标识

	public Long getMarkId() {
		return markId;
	}

	public MessageBox setMarkId(long markId) {
		this.markId = markId;
		return this;
	}

	public String getMsg() {
		return msg;
	}

	public MessageBox setMsg(String msg) {
		this.msg = msg;
		return this;
	}

}
