package com.sean.service.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sean.persist.core.Entity;
import com.sean.persist.core.PersistContext;
import com.sean.persist.dictionary.Dictionary;
import com.sean.service.entity.ReturnParameterEntity;
import com.sean.service.entity.UseDicEntity;

/**
 * 实体列表数据类型fieldwriter
 * @author sean
 */
@SuppressWarnings("unchecked")
public class FieldWriterEntityList extends FieldWriter
{
	private static final FieldWriterEntityList instance = new FieldWriterEntityList();

	private FieldWriterEntityList()
	{
	}

	public static FieldWriterEntityList getInstance()
	{
		return instance;
	}

	@Override
	public void write(StringBuilder json, Map<String, Object> retMap, ReturnParameterEntity param) throws Exception
	{
		String name = param.getName();
		List<Entity> entitys = (List<Entity>) retMap.get(name);

		if (entitys != null)
		{
			json.append("\"").append(name).append("\":[");
			int length = entitys.size();

			Entity entity = null;
			Map<String, Object> data = null;
			Map<String, Object> dicVals = null;
			UseDicEntity[] dics = null;
			UseDicEntity dic = null;
			String[] fields = null;
			String field = null;
			Object val = null;

			for (int i = 0; i < length; i++)
			{
				// 获取实体数据
				entity = entitys.get(i);
				data = entity.getValues();

				// 获取所有数据字典值
				dicVals = new HashMap<>();
				dics = param.getDics();
				for (int j = 0; j < dics.length; j++)
				{
					dic = dics[j];
					Dictionary dictionary = PersistContext.CTX.getDictionary(dic.getDic());
					Object key = data.get(dic.getField());
					if (key != null)
					{
						dictionary.getDicVal(key, dicVals);
					}
				}

				// 开始生成json
				json.append('{');
				fields = param.getFields();
				for (int j = 0; j < fields.length; j++)
				{
					field = fields[j];
					val = data.get(field);
					if (val == null)
					{
						val = dicVals.get(field);
					}
					if (val == null)
					{
						json.append("\"").append(field).append("\":null,");
					}
					else
					{
						json.append("\"").append(field).append("\":\"").append(this.filter(val.toString())).append("\",");
					}
				}
				if (json.charAt(json.length() - 1) == ',')
				{
					json.setCharAt(json.length() - 1, '}');
				}
				else
				{
					json.append('}');
				}
				json.append(',');
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
