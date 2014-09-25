package com.sean.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 对象工具
 * @author Sean
 *
 */
public class ObjectUtil
{
	/**
	 * 创建随机对象
	 * @param cls						对象Class
	 * @return
	 */
	public static Object randomObject(Class<?> cls) throws Exception
	{
		return createRandomObject(cls, null, 0);
	}

	/**
	 * 生成一个随机对象(必须提供set方法)
	 * @param cls						对象Class
	 * @param field						属性
	 * @param isFetch					抓取子对象，最多抓取到2级
	 * @return
	 */
	private static Object createRandomObject(Class<?> cls, Field field, int isFetch) throws Exception
	{
		// 如果是简单数据类型
		if (DataTypeUtil.isBaseDataType(cls))
		{
			// 整数
			if (cls == int.class || cls == long.class || cls == Integer.class || cls == Long.class)
			{
				Random rd = new Random();
				return rd.nextInt(10);
			}
			// 小数
			else if (cls == float.class || cls == double.class || cls == Float.class || cls == Double.class)
			{
				Random rd = new Random();
				return rd.nextFloat();
			}
			// 字符串
			else if (cls == String.class)
			{
				return UUID.randomUUID().toString().subSequence(0, 5);
			}
			// 字符
			else if (cls == char.class)
			{
				Random rd = new Random();
				return rd.nextInt(128);
			}
			// 日期
			else if (cls == Date.class)
			{
				return new Date();
			}
		}
		// 如果是数组
		else if (cls.isArray())
		{
			Object[] arr = new Object[3];
			for (int j = 0; j < arr.length; j++)
			{
				arr[j] = createRandomObject(cls.getComponentType(), field, isFetch + 1);
			}
			return arr;
		}
		// 如果是集合
		else if (Collection.class.isAssignableFrom(cls))
		{
			Type fc = field.getGenericType();
			ParameterizedType pt = (ParameterizedType) fc;
			Class<?> c = (Class<?>) (pt.getActualTypeArguments()[0]);
			List<Object> list = new ArrayList<Object>(3);
			for (int j = 0; j < 3; j++)
			{
				Object o = createRandomObject(c, field, isFetch);
				if (o != null)
				{
					list.add(o);
				}
			}
			return list;
		}
		// 如果是Map
		else if (Map.class.isAssignableFrom(cls))
		{
			Type fc = field.getGenericType();
			ParameterizedType pt = (ParameterizedType) fc;
			Class<?> c = (Class<?>) (pt.getActualTypeArguments()[1]);
			Map<String, Object> map = new HashMap<String, Object>();
			for (int i = 0; i < 3; i++)
			{
				Object o = createRandomObject(c, field, isFetch);
				map.put("key" + i, o);
			}
			return map;
		}
		// 如果是对象递归生成子对象，只抓取到二级子对象
		else
		{
			if (isFetch <= 2)
			{
				Object obj = cls.newInstance();
				Field[] fs = cls.getDeclaredFields();
				Field f = null;
				for (int i = 0; i < fs.length; i++)
				{
					f = fs[i];
					// 跳过static和final属性
					if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
					{
						// 获取setter方法
						Method mth = cls.getMethod("set" + f.getName().toUpperCase().charAt(0) + f.getName().substring(1), f.getType());

						Object o = createRandomObject(f.getType(), f, isFetch + 1);
						if (f.getType().isArray())
						{
							// Object[] os = (Object[])o;
							// mth.invoke(obj, os[0], os[1], os[2]);
						}
						else
						{
							mth.invoke(obj, o);
						}
					}
				}
				return obj;
			}
		}
		return null;
	}

	/**
	 * 反序列化对象
	 * @param b
	 * @return
	 */
	public static Object unSerialize(byte[] b) throws Exception
	{
		ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(b));
		Object obj = input.readObject();
		input.close();
		return obj;
	}
	
	/**
	 * 反序列化对象
	 * @param b
	 * @return
	 */
	public static Object unSerialize(byte[] b, int offset, int len) throws Exception
	{
		ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(b, offset, len));
		Object obj = input.readObject();
		input.close();
		return obj;
	}

	/**
	 * 序列化对象
	 * @param obj
	 * @return
	 */
	public static byte[] serialize(Serializable obj) throws Exception
	{
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		ObjectOutputStream output = new ObjectOutputStream(byteOutput);
		output.writeObject(obj);
		byte[] bytes = byteOutput.toByteArray();
		byteOutput.close();
		output.close();
		return bytes;
	}
}
