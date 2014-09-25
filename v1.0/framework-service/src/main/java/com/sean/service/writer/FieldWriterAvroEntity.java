package com.sean.service.writer;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema.Field;
import org.apache.avro.specific.SpecificRecordBase;

import com.sean.persist.core.PersistContext;
import com.sean.persist.dictionary.Dictionary;
import com.sean.service.entity.ReturnParameterEntity;
import com.sean.service.entity.UseDicEntity;

/**
 * 单一实体数据类型fieldwriter
 * @author sean
 */
public class FieldWriterAvroEntity extends FieldWriter
{
	private static final FieldWriterAvroEntity instance = new FieldWriterAvroEntity();

	private FieldWriterAvroEntity()
	{
	}

	public static FieldWriterAvroEntity getInstance()
	{
		return instance;
	}

	@Override
	public void write(StringBuilder json, Map<String, Object> retMap, ReturnParameterEntity param) throws Exception
	{
		String name = param.getName();
		SpecificRecordBase entity = (SpecificRecordBase) retMap.get(name);

		if (entity != null)
		{
			json.append("\"").append(name).append("\":{");

			// 获取所有数据字典值
			Map<String, String> dicVals = new HashMap<>();
			UseDicEntity[] dics = param.getDics();
			UseDicEntity dic;
			for (int i = 0; i < dics.length; i++)
			{
				dic = dics[i];
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
			String[] fields = param.getFields();
			String field;
			Object val = null;
			for (int i = 0; i < fields.length; i++)
			{
				field = fields[i];
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
		}
		else
		{
			json.append("\"").append(name).append("\":null");
		}
	}
}
