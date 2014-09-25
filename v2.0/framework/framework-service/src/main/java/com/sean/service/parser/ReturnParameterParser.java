package com.sean.service.parser;

import java.lang.reflect.Field;

import com.sean.persist.core.Entity;
import com.sean.persist.dictionary.DictionaryConfig;
import com.sean.service.annotation.FieldsConfig;
import com.sean.service.annotation.ReturnParameterConfig;
import com.sean.service.annotation.UseDicConfig;
import com.sean.service.entity.ReturnParameterEntity;
import com.sean.service.entity.UseDicEntity;
import com.sean.service.enums.Format;
import com.sean.service.writer.FieldWriter;
import com.sean.service.writer.FieldWriterAvroEntity;
import com.sean.service.writer.FieldWriterAvroEntityList;
import com.sean.service.writer.FieldWriterEntity;
import com.sean.service.writer.FieldWriterEntityList;
import com.sean.service.writer.FieldWriterJson;
import com.sean.service.writer.FieldWriterMap;
import com.sean.service.writer.FieldWriterNumeric;
import com.sean.service.writer.FieldWriterString;
import com.sean.service.writer.FieldWriterTable;

/**
 * 返回参数
 * @author sean
 */
public class ReturnParameterParser
{
	public ReturnParameterEntity parse(Object obj, Field field) throws Exception
	{
		ReturnParameterConfig rpc = field.getAnnotation(ReturnParameterConfig.class);
		FieldsConfig fs = field.getAnnotation(FieldsConfig.class);
		UseDicConfig udc = field.getAnnotation(UseDicConfig.class);

		UseDicEntity[] udes = new UseDicEntity[0];
		// 如果是实体，检查配置项
		if (rpc.format() == Format.Entity || rpc.format() == Format.EntityList)
		{
			if (!Entity.class.isAssignableFrom(rpc.entity()))
			{
				throw new RuntimeException("the entity defined in return parameter " + field.get(obj) + " is not an entity");
			}
			if (fs == null || fs.value().length == 0)
			{
				throw new RuntimeException("the entity defined in return parameter " + field.get(obj) + " has not any output fields");
			}

			if (udc != null)
			{
				udes = new UseDicEntity[udc.dic().length];
				for (int i = 0; i < udc.dic().length; i++)
				{
					Class<?> dic = udc.dic()[i];
					if (dic.getAnnotation(DictionaryConfig.class) == null)
					{
						throw new RuntimeException("the dictionary class defined in return parameter " + field.get(obj) + " " + dic.getName()
								+ " is not a dynamic dictionary");
					}
					if (!check(fs.value(), udc.field()[i]))
					{
						throw new RuntimeException("the dictionary field " + udc.field()[i] + " was not found in the fields of return parameter "
								+ field.get(obj));
					}

					udes[i] = new UseDicEntity(udc.field()[i], udc.dic()[i].getName());
				}
			}
		}
		else if(rpc.format() == Format.AvroEntity || rpc.format() == Format.AvroEntityList)
		{
			if (udc != null)
			{
				udes = new UseDicEntity[udc.dic().length];
				for (int i = 0; i < udc.dic().length; i++)
				{
					Class<?> dic = udc.dic()[i];
					if (dic.getAnnotation(DictionaryConfig.class) == null)
					{
						throw new RuntimeException("the dictionary class defined in return parameter " + field.get(obj) + " " + dic.getName()
								+ " is not a dynamic dictionary");
					}
					udes[i] = new UseDicEntity(udc.field()[i], udc.dic()[i].getName());
				}	
			}
		}

		FieldWriter fieldWriter = null;
		switch (rpc.format())
		{
		case Numeric:
			fieldWriter = FieldWriterNumeric.getInstance();
			break;
		case String:
			fieldWriter = FieldWriterString.getInstance();
			break;
		case Json:
			fieldWriter = FieldWriterJson.getInstance();
			break;
		case Map:
			fieldWriter = FieldWriterMap.getInstance();
		case Table:
			fieldWriter = FieldWriterTable.getInstance();
			break;
		case Entity:
			fieldWriter = FieldWriterEntity.getInstance();
			break;
		case EntityList:
			fieldWriter = FieldWriterEntityList.getInstance();
			break;
		case AvroEntity:
			fieldWriter = FieldWriterAvroEntity.getInstance();
			break;
		case AvroEntityList:
			fieldWriter = FieldWriterAvroEntityList.getInstance();
			break;
		default:
			break;
		}

		String[] fields = new String[0];
		if (fs != null)
		{
			fields = fs.value();
		}
		ReturnParameterEntity pe = new ReturnParameterEntity(field.get(obj).toString(), rpc.format(), rpc.entity(), fields, udes, rpc.description(),
				fieldWriter);
		return pe;
	}

	private boolean check(String[] fields, String field)
	{
		for (int i = 0; i < fields.length; i++)
		{
			if (field.equals(fields[i]))
			{
				return true;
			}
		}
		return false;
	}
}
