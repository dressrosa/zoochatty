/**
 * 唯有读书,不慵不扰 
 **/
package com.xiaoyu.core.zk;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.data.Stat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoyu.biz.bean.MessageBox;
import com.xiaoyu.biz.chat.Chatting;
import com.xiaoyu.core.serializer.JsonSerializer;

/**
 * 2017年4月12日下午5:06:24
 * 
 * @author xiaoyu
 * @description 对zkclient功能封装
 */
public class ZooWrapper {

	Logger logger = Logger.getLogger("ZooWrapper");

	private static final boolean isDebug = false;

	private static final Integer SESSION_TIMEOUT = 5000;//// 临时节点消失的时间
	private static final Integer CONNECTION_TIMEOUT = 5000;
	private static final String HOST = "localhost";// TODO
	private ZkClient client;

	private Chatting chat;

	private AtomicInteger sendVersion = new AtomicInteger(0);// 数据的版本号
	private AtomicInteger getVersion = new AtomicInteger(0);// 数据的版本号

	public ZooWrapper(Chatting chat) {
		client = new ZkClient(HOST, CONNECTION_TIMEOUT, SESSION_TIMEOUT, new JsonSerializer());
		this.chat = chat;
	}

	public void createPersistent(String path) {
		if (!client.exists(path)) {
			client.createPersistent(path);
			resetSendVersion();
			resetGetVersion();
		}
	}

	public void createPersistent(String path1, String path2) {
		if (!client.exists(path1))
			client.createPersistent(path1);
		if (!client.exists(path2))
			client.createPersistent(path2);
	}

	public void createEphemeral(String path) {
		if (!client.exists(path)) {
			client.createEphemeral(path);

		}
	}

	private void resetSendVersion() {
		resetSendVersion(0);
	}

	private void resetSendVersion(int num) {
		sendVersion.set(num);
	}

	private void resetGetVersion() {
		resetGetVersion(0);
	}

	private void resetGetVersion(int num) {
		getVersion.set(num);
	}

	public void createEphemeral(String path1, String path2) {
		if (!client.exists(path1)) {
			client.createEphemeral(path1);

		}
		if (!client.exists(path2)) {
			client.createEphemeral(path2);
		}
	}

	private boolean isOver = false;

	public boolean isOver() {
		return isOver;
	}

	public void setOvered() {
		this.isOver = true;
		client.close();
	}

	private MessageBox convertFrom(Object data) {
		return JSON.toJavaObject((JSONObject) data, MessageBox.class);
	}

	public void subscribeDataChanges(final String path) {
		createPersistent(path);// 检测是否已经创建
		// Set<Op> set = Collections.EMPTY_SET;
		// Op e = Op.check(path, );
		// set.add(e);
		// Iterable<Op> ops = null;
		// client.multi(ops);
		Object data = this.readData(path);
		if (data != null) {
			MessageBox box = null;
			try {
				box = convertFrom(data);
				chat.handleMessage(box);
			} finally {
				if (box != null) {
					box = null;// 将消息置空
					writeData(path, box);// 置空动作会再次触发dataChange
				}
			}

		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				client.subscribeDataChanges(path, new IZkDataListener() {
					@Override
					public void handleDataDeleted(String dataPath) throws Exception {
					}

					@Override
					public void handleDataChange(String dataPath, Object data) throws Exception {
						MessageBox box = null;
						try {
							box = convertFrom(data);
							if (box != null && box.getMsg() != null)// 二次触发判断
								chat.handleMessage(box);
						} catch (Exception e) {
							// 认为是离线
						} finally {
							if (box != null && box.getMsg() != null) {
								box.setMsg(null).setMarkId(new Date().getTime());// 将消息置空
								writeData(path, box);// 置空动作会再次触发dataChange
							}
						}
					}
				});
				while (!isOver())
					;// 未结束一直循环监听
				System.out.println("线程结束...");
			}
		}, "t-data-" + path);
		t.start();
	}

	// 发送消息是发送到对方节点下面代表自己的子节点
	public void writeData(String path, Object data) {
		createPersistent(path);
		// client.writeData(path, data.getBytes());

		// List<Op> list = new ArrayList<>();

		// list.add(Op.check(path, version.get()));
		// list.add(Op.setData(path, JSON.toJSONBytes(data,
		// SerializerFeature.EMPTY), version.incrementAndGet()));
		// client.multi(list);
		// List<OpResult> result = client.multi(list);
		// System.out.println("哈哈");
		// for (OpResult r : result) {
		// System.out.println("结果:" + r.getType());
		// }
		Stat s = new Stat();
		client.readData(path, s);
		if (isDebug)
			logger.info(path + "当前服务器版本:" + s.getVersion() + " 本地版本" + sendVersion.get());
		final MessageBox box = (MessageBox) data;
		Stat stat = new Stat();
		MessageBox serverBox = convertFrom(client.readData(path, stat));
		if (serverBox == null || serverBox.getMsg() == null || box == null || box.getMsg() == null) //
			// 此时是刚刚启动聊天
			sendVersion.set(stat.getVersion());// 取服务器version于本地

		if (serverBox == null) {// 服务器上为空,说明没有发过消息
			stat = client.writeDataReturnStat(path, box, sendVersion.getAndIncrement());
			if (isDebug)
				logger.info(path + "首次发送数据版本:服务器:" + stat.getVersion() + ":本地:" + sendVersion.get());
		} else {
			if (serverBox.getMsg() == null) {// 服务器为置空消息,说明对方正常提取成功
				stat = client.writeDataReturnStat(path, box, sendVersion.getAndIncrement());
				if (isDebug)
					logger.info(path + "在线数据版本:服务器:" + stat.getVersion() + ":本地:" + sendVersion.get());
			} else {
				if (box == null) {// 为提取离线消息后,清空消息,为对方的发送动作
					client.writeData(path, box);
					if (isDebug)
						logger.info(path + "清空离线消息");
				} else if (box.getMsg() == null) {// 为提取消息的置空动作,即为对方的发送动作
					client.writeData(path, box);
					if (isDebug)
						logger.info(path + "置空消息");
				} else {// 有离线消息
					serverBox.setMsg(serverBox.getMsg().concat("\n" + box.getMsg()));
					if (client.readData(path) != null) {// 再次尝试,如果还不为空,代表对方依然离线,保持开始发送时间不变
						// 这里依然有可能消息被提取了,但是这里被视为离线,导致上一条消息重复
						stat = client.writeDataReturnStat(path, serverBox, sendVersion.getAndIncrement());
						if (isDebug)
							logger.info(path + "离线数据版本:服务器" + stat.getVersion() + ":本地:" + sendVersion.get());
					} else {// 变为正常发送
						stat = client.writeDataReturnStat(path, box, sendVersion.getAndIncrement());
						if (isDebug)
							logger.info(path + "在线数据版本:服务器:" + stat.getVersion() + ":本地:" + sendVersion.get());
					}
				}

			}

		}

	}

	public void subscribeChildChanges(final String path) {
		createEphemeral(path);// 检测是否已经创建
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				client.subscribeChildChanges(path, new IZkChildListener() {
					@Override
					public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
						System.out.println(parentPath + "的孩子变了:" + currentChilds);
					}
				});
				for (;;)
					;
			}
		}, "t-child-" + path);
		t.start();
	}

	public Object readData(String path) {
		// Stat stat = new Stat();
		// Object obj = client.readData(path, stat);
		// return obj;

		return client.readData(path);
	}
}
