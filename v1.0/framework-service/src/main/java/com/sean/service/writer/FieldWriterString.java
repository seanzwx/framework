package com.sean.service.writer;

import java.util.Map;

import com.sean.service.entity.ReturnParameterEntity;

/**
 * 字符串数据类型fieldwriter
 * @author sean
 */
public class FieldWriterString extends FieldWriter
{
	private static final FieldWriterString instance = new FieldWriterString();

	private FieldWriterString()
	{
	}

	public static FieldWriterString getInstance()
	{
		return instance;
	}

	@Override
	public void write(StringBuilder json, Map<String, Object> retMap, ReturnParameterEntity param)
	{
		String name = param.getName();
		Object val = retMap.get(name);
		if (val != null)
		{
			json.append("\"").append(name).append("\":\"").append(this.filter(val.toString())).append("\"");
		}
		else
		{
			json.append("\"").append(name).append("\":null");
		}
	}
}
