package com.sean.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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

	/**
	* json字符串的格式化
	* @author peiyuxin
	* @param json 需要格式的json串
	* @param fillStringUnit每一层之前的占位符号比如空格 制表符 
	* @return
	*/
	public static String formatJson(String json, String fillStringUnit)
	{
		if (json == null || json.trim().length() == 0)
		{
			return null;
		}

		int fixedLenth = 0;
		ArrayList<String> tokenList = new ArrayList<String>();
		{
			String jsonTemp = json;
			// 预读取
			while (jsonTemp.length() > 0)
			{
				String token = getToken(jsonTemp);
				jsonTemp = jsonTemp.substring(token.length());
				token = token.trim();
				tokenList.add(token);
			}
		}

		for (int i = 0; i < tokenList.size(); i++)
		{
			String token = tokenList.get(i);
			int length = token.getBytes().length;
			if (length > fixedLenth && i < tokenList.size() - 1 && tokenList.get(i + 1).equals(":"))
			{
				fixedLenth = length;
			}
		}

		StringBuilder buf = new StringBuilder();
		int count = 0;
		for (int i = 0; i < tokenList.size(); i++)
		{

			String token = tokenList.get(i);

			if (token.equals(","))
			{
				buf.append(token);
				if (!tokenList.get(i + 1).equals("{"))
				{
					doFill(buf, count, fillStringUnit);
				}
				continue;
			}
			if (token.equals(":"))
			{
				buf.append(" ").append(token).append(" ");
				continue;
			}
			if (token.equals("{"))
			{
				String nextToken = tokenList.get(i + 1);
				if (nextToken.equals("}"))
				{
					i++;
					buf.append("{}");
				}
				else
				{
					doFill(buf, count, fillStringUnit);
					count++;
					buf.append(token);
					doFill(buf, count, fillStringUnit);
				}
				continue;
			}
			if (token.equals("}"))
			{
				count--;
				doFill(buf, count, fillStringUnit);
				buf.append(token);
				continue;
			}
			if (token.equals("["))
			{
				doFill(buf, count, fillStringUnit);
				String nextToken = tokenList.get(i + 1);
				if (nextToken.equals("]"))
				{
					i++;
					buf.append("[]");
				}
				else
				{
					count++;
					buf.append(token);
					doFill(buf, count, fillStringUnit);
				}
				continue;
			}
			if (token.equals("]"))
			{
				count--;
				doFill(buf, count, fillStringUnit);
				buf.append(token);
				continue;
			}

			buf.append(token);
//			// 左对齐
//			if (i < tokenList.size() - 1 && tokenList.get(i + 1).equals(":"))
//			{
//				int fillLength = fixedLenth - token.getBytes().length;
//				if (fillLength > 0)
//				{
//					for (int j = 0; j < fillLength; j++)
//					{
//						buf.append(" ");
//					}
//				}
//			}
		}
		return buf.toString();
	}

	private static String getToken(String json)
	{
		StringBuilder buf = new StringBuilder();
		boolean isInYinHao = false;
		while (json.length() > 0)
		{
			String token = json.substring(0, 1);
			json = json.substring(1);

			if (!isInYinHao
					&& (token.equals(":") || token.equals("{") || token.equals("}") || token.equals("[") || token.equals("]") || token.equals(",")))
			{
				if (buf.toString().trim().length() == 0)
				{
					buf.append(token);
				}

				break;
			}

			if (token.equals("\\"))
			{
				buf.append(token);
				buf.append(json.substring(0, 1));
				json = json.substring(1);
				continue;
			}
			if (token.equals("\""))
			{
				buf.append(token);
				if (isInYinHao)
				{
					break;
				}
				else
				{
					isInYinHao = true;
					continue;
				}
			}
			buf.append(token);
		}
		return buf.toString();
	}

	private static void doFill(StringBuilder buf, int count, String fillStringUnit)
	{
		buf.append("\n");
		for (int i = 0; i < count; i++)
		{
			buf.append(fillStringUnit);
		}
	}
}
