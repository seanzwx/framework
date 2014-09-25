package com.sean.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class DataTypeUtil
{
	/**
	 * 判断一个类是否为基本数据类型。
	 */
	public static boolean isBaseDataType(Class<?> cls)
	{
		return 
		(
			cls.isPrimitive() ||
			cls == String.class || 
			cls == Integer.class || 
			cls == Long.class || 
			cls == Double.class || 
			cls == Float.class || 
			cls == Date.class || 
			cls == Boolean.class || 
			Enum.class.isAssignableFrom(cls) ||
			cls == Byte.class || 
			cls == Character.class || 
			cls == Short.class || 
			cls == BigDecimal.class || 
			cls == BigInteger.class
		);
	}
}
