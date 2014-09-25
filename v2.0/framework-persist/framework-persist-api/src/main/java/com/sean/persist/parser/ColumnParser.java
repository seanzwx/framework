package com.sean.persist.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.sean.persist.annotation.ColumnConfig;
import com.sean.persist.entity.ColumnEntity;

/**
 * column解析器
 * @author sean
 */
public class ColumnParser
{
	public List<ColumnEntity> parse(Class<?> cls)
	{
		List<ColumnEntity> columns = new ArrayList<ColumnEntity>();

		Field[] fs = cls.getDeclaredFields();
		Field f = null;
		String columnStr = null;
		for (int i = 0; i < fs.length; i++)
		{
			f = fs[i];
			// 跳过static和final
			if (!Modifier.isStatic(f.getModifiers()) && !Modifier.isFinal(f.getModifiers()))
			{
				ColumnConfig cc = f.getAnnotation(ColumnConfig.class);
				if (cc != null)
				{
					columnStr = cc.column();
					if (columnStr == null || columnStr.isEmpty())
					{
						columnStr = f.getName();
					}
					ColumnEntity column = new ColumnEntity(columnStr, cc.primaryKey(), cc.sequenceName(), cc.descr(), f);
					columns.add(column);
				}
			}
		}
		return columns;
	}
}
