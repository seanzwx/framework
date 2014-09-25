package com.sean.persist.core;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 实体抽象基类
 * @author Sean
 *
 */
public abstract class Entity
{
	/**
	 * 读取主键
	 */
	public abstract Object getKey();

	/**
	 * 设置主键值
	 * @param key				主键值
	 */
	public abstract void setKey(Object key);

	/**
	 * 生成唯一的rowkey，确保在分布式环境中必须唯一，在关系型数据库中该字段暂不使用
	 * <p>该方法生成的key必须与主键的数据类型相同</p>
	 * @return
	 */
	public Object generateRowKey()
	{
		return DigestUtils.md5Hex(UUID.randomUUID().toString());
	}

	/**
	 * 读取实体的值
	 * @return					值数组
	 */
	public abstract Map<String, Object> getValues();

	/**
	 * 设置实体的值
	 * @param vals				值map,key为identifier.fieldName
	 */
	public abstract void setValues(EntityValue vals);
}
