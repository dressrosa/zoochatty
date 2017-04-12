/**
 * 唯有读书,不慵不扰 
 **/
package com.xiaoyu.core.serializer;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 2017年4月12日下午4:52:15
 * 
 * @author xiaoyu
 * @description 采用fastjson进行序列化
 */
public class JsonSerializer implements ZkSerializer {

	@Override
	public byte[] serialize(Object data) throws ZkMarshallingError {
		return JSON.toJSONBytes(data, SerializerFeature.EMPTY);

	}

	@Override
	public Object deserialize(byte[] bytes) throws ZkMarshallingError {
		return JSON.parse(bytes, Feature.IgnoreNotMatch);
	}

}
