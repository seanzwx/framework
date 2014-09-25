package com.sean.service.writer;

import java.util.HashMap;
import java.util.Map;

import com.sean.persist.core.Entity;
import com.sean.persist.core.PersistContext;
import com.sean.persist.dictionary.Dictionary;
import com.sean.service.entity.ReturnParameterEntity;
import com.sean.service.entity.UseDicEntity;

/**
 * 单一实体数据类型fieldwriter
 * @author sean
 */
public class FieldWriterEntity extends FieldWriter
{
	private static final FieldWriterEntity instance = new FieldWriterEntity();

	private FieldWriterEntity()
	{
	}

	public static FieldWriterEntity getInstance()
	{
		return instance;
	}

	@Override
	public void write(StringBuilder json, Map<String, Object> retMap, ReturnParameterEntity param) throws Exception
	{
		String name = param.getName();
		Entity entity = (Entity) retMap.get(name);

		if (entity != null)
		{
			json.append("\"").append(name).append("\":{");
			Map<String, Object> data = entity.getValues();

			// 获取所有数据字典值
			Map<String, Object> dicVals = new HashMap<>();
			UseDicEntity[] dics = param.getDics();
			UseDicEntity dic;
			for (int i = 0; i < dics.length; i++)
			{
				dic = dics[i];
				Dictionary dictionary = PersistContext.CTX.getDictionary(dic.getDic());
				Object key = data.get(dic.getField());
				if (key != null)
				{
					dictionary.getDicVal(key, dicVals);
				}
			}

			// 开始生成json
			String[] fields = param.getFields();
			String field;
			Object val;
			for (int i = 0; i < fields.length; i++)
			{
				field = fields[i];
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
		}
		else
		{
			json.append("\"").append(name).append("\":null");
		}
	}
}
