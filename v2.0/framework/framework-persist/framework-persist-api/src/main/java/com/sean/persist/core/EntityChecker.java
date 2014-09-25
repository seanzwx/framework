package com.sean.persist.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.common.util.DataTypeUtil;
import com.sean.log.core.LogFactory;
import com.sean.persist.entity.EntityEntity;
import com.sean.persist.enums.L;

/**
 * Entity映射校验器
 * @author Sean
 */
public abstract class EntityChecker
{
	Map<String, List<Class<?>>> datas = new HashMap<String, List<Class<?>>>();
	private static final Logger logger = LogFactory.getLogger(L.Persist);

	/**
	 * 初始化数据库数据类型
	 */
	public EntityChecker()
	{
		// 整形
		List<Class<?>> numberCls = new ArrayList<Class<?>>();
		numberCls.add(int.class);
		numberCls.add(Integer.class);
		numberCls.add(long.class);
		numberCls.add(Long.class);
		// 单精度浮点型
		List<Class<?>> floatCls = new ArrayList<Class<?>>();
		floatCls.add(float.class);
		floatCls.add(Float.class);
		// 双精度浮点型
		List<Class<?>> doubleCls = new ArrayList<Class<?>>();
		doubleCls.add(double.class);
		doubleCls.add(Double.class);
		// 字符串型
		List<Class<?>> stringCls = new ArrayList<Class<?>>();
		stringCls.add(String.class);
		stringCls.add(StringBuilder.class);
		stringCls.add(StringBuffer.class);
		stringCls.add(char.class);
		// 日期型
		List<Class<?>> dateCls = new ArrayList<Class<?>>();
		dateCls.add(Date.class);
		dateCls.add(Timestamp.class);
		dateCls.add(java.sql.Date.class);

		List<Class<?>> byteCls = new ArrayList<Class<?>>();
		byteCls.add(byte[].class);

		datas.put("int", numberCls);
		datas.put("integer", numberCls);

		datas.put("bigint", numberCls);
		datas.put("numeric", numberCls);
		datas.put("number", numberCls);

		datas.put("float", floatCls);
		datas.put("doubule", doubleCls);

		datas.put("varchar", stringCls);
		datas.put("narchar", stringCls);
		datas.put("char", stringCls);
		datas.put("varchar2", stringCls);
		datas.put("nvarchar2", stringCls);

		datas.put("date", dateCls);
		datas.put("datetime", dateCls);
		datas.put("timestamp", dateCls);

		datas.put("raw", byteCls);
	}

	/**
	 * 释放临时资源
	 */
	public void releaseTmpResource()
	{
		if (datas != null)
		{
			this.datas.clear();
			this.datas = null;
		}

		logger.info("EntityChecker release temporary resource successfully");
	}

	/**
	 * 验证Entity的数据映射
	 */
	public abstract void checkMapping(Map<String, EntityEntity> entitys) throws Exception;

	/**
	 * 数据类型验证
	 * @param type				数据类型
	 * @param dataType			数据库数据类型字符串
	 * @return
	 */
	protected boolean check(Class<?> type, String dataType) throws Exception
	{
		// 只验证基本数据类型
		if (DataTypeUtil.isBaseDataType(type))
		{
			List<Class<?>> list = datas.get(dataType.toLowerCase());
			for (int i = 0; i < list.size(); i++)
			{
				if (list.get(i) == type)
				{
					return true;
				}
			}
			return false;
		}
		return true;
	}

	protected class Column
	{
		public String name;
		public String dataType;
		public String columnKey;
	}
}
