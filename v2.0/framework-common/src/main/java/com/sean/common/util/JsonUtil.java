package com.sean.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * json转换工具
 * @author Sean
 *
 */
@SuppressWarnings("unchecked")
public class JsonUtil
{	
	/**
	 * 对象转json(任何对象包括集合、数组、Map,必须提供get方法)
	 * @param obj
	 * @return
	 */
	public static String toJson(Object obj) throws Exception
	{
		StringBuilder json = new StringBuilder(1024);
		// 如果为null
		if (obj == null)
		{
			json.append("null");
		}
		// 如果是数组
		else if (obj.getClass().isArray())
		{
			json.append('[');
			Object[] os = (Object[]) obj;
			for (int j = 0; j < os.length; j++)
			{
				json.append(toJson(os[j])).append(',');
			}
			if (os.length > 1)
			{
				json.setCharAt(json.length() - 1, ']');
			}
			else
			{
				json.append(']');
			}
		}
		// 如果是集合
		else if (Collection.class.isAssignableFrom(obj.getClass()))
		{
			json.append('[');
			Collection<?> cl = (Collection<?>) obj;
			for (Iterator<?> it = cl.iterator(); it.hasNext();)
			{
				json.append(toJson(it.next())).append(',');
			}
			if (cl.size() > 0)
			{
				json.setCharAt(json.length() - 1, ']');
			}
			else
			{
				json.append(']');
			}
		}
		// 如果是Map
		else if (Map.class.isAssignableFrom(obj.getClass()))
		{
			Map<String, Object> cl = (Map<String, Object>) obj;
			String key;
			json.append("{");
			for (Iterator<String> it = cl.keySet().iterator(); it.hasNext();)
			{
				key = it.next();
				json.append('"').append(key).append("\":");
				json.append(toJson(cl.get(key))).append(',');
			}
			if (cl.keySet().size() > 0)
			{
				json.setCharAt(json.length() - 1, '}');
			}
			else
			{
				json.append('}');
			}

		}
		// 如果是基本数据类型
		else if (DataTypeUtil.isBaseDataType(obj.getClass()))
		{
			// 如果是字符串，则进行过滤
			if (obj.getClass() == String.class)
			{
				json.append('"').append(filter(obj.toString())).append('"');
			}
			else
			{
				json.append('"').append(obj.toString()).append('"');
			}
		}
		// 如果是对象
		else
		{
			json.append('{');
			Field[] fs = obj.getClass().getDeclaredFields();
			Field f = null;
			for (int i = 0; i < fs.length; i++)
			{
				f = fs[i];
				// 跳过static和final属性
				if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
				{
					f.setAccessible(true);
					json.append('"').append(f.getName()).append("\":");
					// 递归调用
					json.append(toJson(f.get(obj))).append(',');
				}
			}
			json.setCharAt(json.length() - 1, '}');
		}
		return json.toString();
	}
	
	/**
	 * 过滤特殊字符
	 * @param str
	 * @return
	 */
	private static String filter(String str)
	{
		int length = str.length();
		StringBuilder sb = new StringBuilder(length * 2);
		char c;
		for (int i = 0; i < length; i++)
		{
			c = str.charAt(i);
			switch (c)
			{
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
