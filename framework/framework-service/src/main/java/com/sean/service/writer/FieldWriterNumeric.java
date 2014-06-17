package com.sean.service.writer;

import java.util.Map;

import com.sean.service.entity.ReturnParameterEntity;

/**
 * 数值数据类型fieldwriter
 * @author sean
 *
 */
public class FieldWriterNumeric extends FieldWriter
{
	private static final FieldWriterNumeric instance = new FieldWriterNumeric();

	private FieldWriterNumeric()
	{
	}

	public static FieldWriterNumeric getInstance()
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
			json.append("\"").append(name).append("\":\"").append(val.toString()).append("\"");
		}
		else
		{
			json.append("\"").append(name).append("\":null");
		}
	}
}
