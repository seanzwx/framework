package com.sean.service.writer;

import java.util.Map;

import com.sean.service.entity.ReturnParameterEntity;

public abstract class FieldWriter
{
	protected String filter(String str)
	{
		int length = str.length();
		StringBuilder sb = new StringBuilder(length * 2);
		char c;
		for (int i = 0; i < length; i++)
		{
			c = str.charAt(i);
			switch (c)
			{
			case '"':
				sb.append("\\\\\"");
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

	public abstract void write(StringBuilder json, Map<String, Object> retMap, ReturnParameterEntity param) throws Exception;
}
