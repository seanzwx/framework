package com.sean.service.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema.Field;
import org.apache.avro.specific.SpecificRecordBase;

import com.sean.persist.core.PersistContext;
import com.sean.persist.dictionary.Dictionary;
import com.sean.service.entity.ReturnParameterEntity;
import com.sean.service.entity.UseDicEntity;

/**
 * 实体列表数据类型fieldwriter
 * @author sean
 */
@SuppressWarnings("unchecked")
public class FieldWriterAvroEntityList extends FieldWriter
{
	private static final FieldWriterAvroEntityList instance = new FieldWriterAvroEntityList();

	private FieldWriterAvroEntityList()
	{
	}

	public static FieldWriterAvroEntityList getInstance()
	{
		return instance;
	}

	@Override
	public void write(StringBuilder json, Map<String, Object> retMap, ReturnParameterEntity param) throws Exception
	{
		String name = param.getName();
		List<SpecificRecordBase> entitys = (List<SpecificRecordBase>) retMap.get(name);

		if (entitys != null)
		{
			json.append("\"").append(name).append("\":[");
			int length = entitys.size();

			SpecificRecordBase entity = null;
			Map<String, String> dicVals = null;
			UseDicEntity[] dics = null;
			UseDicEntity dic = null;
			String[] fields = null;
			String field = null;
			Object val = null;

			for (int i = 0; i < length; i++)
			{
				// 获取实体数据
				entity = entitys.get(i);

				// 获取所有数据字典值
				dicVals = new HashMap<>();
				dics = param.getDics();
				for (int j = 0; j < dics.length; j++)
				{
					dic = dics[j];
					Dictionary dictionary = PersistContext.CTX.getDictionary(dic.getDic());

					try
					{
						Object key = entity.get(entity.getSchema().getField(dic.getField()).pos());
						dictionary.getDicVal(key, dicVals);
					}
					catch (AvroRuntimeException e)
					{
					}
				}

				// 开始生成json
				json.append('{');
				fields = param.getFields();
				for (int j = 0; j < fields.length; j++)
				{
					field = fields[j];
					val = null;
					try
					{
						Field f = entity.getSchema().getField(field);
						if (f != null)
						{
							val = entity.get(f.pos());
						}
					}
					catch (AvroRuntimeException e)
					{
					}
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
