package com.sean.service.parser;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import com.sean.persist.annotation.ColumnConfig;
import com.sean.persist.core.Entity;
import com.sean.persist.dictionary.DictionaryConfig;
import com.sean.persist.dictionary.DictionaryKeyConfig;
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
		String[] fields = new String[0];

		UseDicConfig[] udcs = field.getAnnotationsByType(UseDicConfig.class);

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

			// 检查*通配符
			boolean contains = false;
			for (String f : fs.value())
			{
				if (f.equals("*"))
				{
					contains = true;
					break;
				}
			}
			if (contains)
			{
				if (fs.value().length > 1)
				{
					throw new RuntimeException("return parameter fields can not contain any field any more when contains * wildcard");
				}
			}

			// 统计输出 fields
			Set<String> set = new HashSet<>();
			if (fs.value()[0].equals("*"))
			{
				// 先加入实体所有域
				for (Field it : rpc.entity().getDeclaredFields())
				{
					if (it.getAnnotation(ColumnConfig.class) != null)
					{
						set.add(it.getName());
					}
				}

				// 加入所有字典
				for (UseDicConfig it : udcs)
				{
					Class<?> dic = it.dic();
					DictionaryKeyConfig[] keys = dic.getAnnotationsByType(DictionaryKeyConfig.class);
					for (DictionaryKeyConfig k : keys)
					{
						set.add(k.key());
					}
				}
			}
			else
			{
				// 加入value
				for (String it : fs.value())
				{
					set.add(it);
				}
			}
			// 过滤exclude
			for (String it : fs.exclude())
			{
				set.remove(it);
			}
			fields = set.toArray(new String[set.size()]);

			// 检查数据字典
			udes = new UseDicEntity[udcs.length];
			for (int i = 0; i < udcs.length; i++)
			{
				UseDicConfig it = udcs[i];
				Class<?> dic = it.dic();
				if (dic.getAnnotation(DictionaryConfig.class) == null)
				{
					throw new RuntimeException("the dictionary class defined in return parameter " + field.get(obj) + " " + dic.getName()
							+ " is not a dynamic dictionary");
				}
				if (!check(fields, it.field()))
				{
					throw new RuntimeException("the dictionary field " + it.field() + " was not found in the fields of return parameter "
							+ field.get(obj));
				}

				udes[i] = new UseDicEntity(it.field(), it.dic().getName());
			}
		}
		// TODO Avro实体实现不完善, exclude 和实体校验未实现
		else if (rpc.format() == Format.AvroEntity || rpc.format() == Format.AvroEntityList)
		{
			// 统计输出 fields
			Set<String> set = new HashSet<>();
			// 加入value
			for (String it : fs.value())
			{
				set.add(it);
			}
			fields = set.toArray(new String[set.size()]);

			udes = new UseDicEntity[udcs.length];
			for (int i = 0; i < udcs.length; i++)
			{
				UseDicConfig it = udcs[i];
				Class<?> dic = it.dic();
				if (dic.getAnnotation(DictionaryConfig.class) == null)
				{
					throw new RuntimeException("the dictionary class defined in return parameter " + field.get(obj) + " " + dic.getName()
							+ " is not a dynamic dictionary");
				}
				udes[i] = new UseDicEntity(it.field(), it.dic().getName());
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

		ReturnParameterEntity pe = new ReturnParameterEntity(field.get(obj).toString(), rpc.format(), rpc.entity(), fields, udes, rpc.descr(),
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
