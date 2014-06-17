package com.sean.service.writer;

import java.util.Map;

import com.sean.service.entity.ReturnParameterEntity;

/**
 * Map数据类型fieldwriter
 * @author sean
 */
@SuppressWarnings("unchecked")
public class FieldWriterMap extends FieldWriter
{
	private static final FieldWriterMap instance = new FieldWriterMap();

	private FieldWriterMap()
	{
	}

	public static FieldWriterMap getInstance()
	{
		return instance;
	}

	@Override
	public void write(StringBuilder json, Map<String, Object> retMap, ReturnParameterEntity param)
	{
		String name = param.getName();
		Map<String, String> map = (Map<String, String>) retMap.get(name);

		if (map != null)
		{
			json.append("\"").append(name).append("\":{");
			String[] fields = param.getFields();
			String field;
			for (int i = 0; i < fields.length; i++)
			{
				field = fields[i];
				json.append("\"").append(field).append("\":\"").append(this.filter(map.get(field))).append("\",");
			}
			if (json.charAt(json.length() - 1) == ',')
			{
				json.setCharAt(json.length() - 1, '}');
			}
			else
			{
				json.append('}');
			}
		}
		else
		{
			json.append("\"").append(name).append("\":null");
		}
	}
}
