package com.sean.service.writer;

import java.util.List;
import java.util.Map;

import com.sean.service.entity.ReturnParameterEntity;

/**
 * Table数据类型fieldwriter
 * @author sean
 */
@SuppressWarnings("unchecked")
public class FieldWriterTable extends FieldWriter
{
	private static final FieldWriterTable instance = new FieldWriterTable();

	private FieldWriterTable()
	{
	}

	public static FieldWriterTable getInstance()
	{
		return instance;
	}

	@Override
	public void write(StringBuilder json, Map<String, Object> retMap, ReturnParameterEntity param)
	{
		String name = param.getName();
		String[] fields = param.getFields();
		List<Map<String, String>> table = (List<Map<String, String>>) retMap.get(name);

		if (table != null)
		{
			json.append("\"").append(name).append("\":[");
			for (Map<String, String> it : table)
			{
				json.append("{");
				for (String field : fields)
				{
					json.append("\"").append(field).append("\":\"").append(this.filter(it.get(field))).append("\",");
				}
				if (json.charAt(json.length() - 1) == ',')
				{
					json.setCharAt(json.length() - 1, '}');
					json.append(',');
				}
				else
				{
					json.append("},");
				}
			}

			if (json.charAt(json.length() - 1) == ',')
			{
				json.setCharAt(json.length() - 1, ']');
			}
			else
			{
				json.append(']');
			}
		}
		else
		{
			json.append("\"").append(name).append("\":null");
		}
	}
}
