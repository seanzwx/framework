package com.sean.persist.parser;

import java.util.List;

import com.sean.persist.annotation.EntityConfig;
import com.sean.persist.entity.ColumnEntity;
import com.sean.persist.entity.EntityEntity;

/**
 * entity解析器
 * @author sean
 *
 */
public class EntityParser
{
	public EntityEntity parse(Class<?> cls)
	{
		ColumnParser columnParser = new ColumnParser();
		EntityConfig ec = cls.getAnnotation(EntityConfig.class);

		List<ColumnEntity> columns = columnParser.parse(cls);
		EntityEntity entity = new EntityEntity(ec.tableName(), ec.dataSource(), ec.cache(), columns, ec.cachePolicy(),
				ec.partition(), cls);
		return entity;
	}
}
