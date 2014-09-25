package com.sean.service.writer;

import java.util.Map;

import com.sean.service.entity.ReturnParameterEntity;

/**
 * json数据类型fieldwriter
 * @author sean
 */
public class FieldWriterJson extends FieldWriter
{
	private static final FieldWriterJson instance = new FieldWriterJson();

	private FieldWriterJson()
	{
	}

	public static FieldWriterJson getInstance()
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
			json.append("\"").append(name).append("\":").append(val.toString());
		}
		else
		{
			json.append("\"").append(name).append("\":null");
		}
	}
}
