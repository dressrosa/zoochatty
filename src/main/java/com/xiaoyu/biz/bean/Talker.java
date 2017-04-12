/**
 * 唯有读书,不慵不扰 
 **/
package com.xiaoyu.biz.bean;

/**2017年4月12日下午4:57:40
 * @author xiaoyu
 * @description 用户
 */
public class Talker {

	private String userId;
	private String nickname;
	private String avatar;

	public String getUserId() {
		return userId;
	}

	public Talker setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	public String getNickname() {
		return nickname;
	}

	public Talker setNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}

	public String getAvatar() {
		return avatar;
	}

	public Talker setAvatar(String avatar) {
		this.avatar = avatar;
		return this;
	}

}
