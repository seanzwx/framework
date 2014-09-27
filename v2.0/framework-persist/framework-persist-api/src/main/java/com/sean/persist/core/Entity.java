package com.sean.persist.core;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.sean.persist.annotation.ColumnConfig;
import com.sean.persist.annotation.EntityConfig;

/**
 * 实体抽象基类
 * @author Sean
 *
 */
public abstract class Entity
{
	/**
	 * 读取主键
	 */
	public abstract Object getKey();

	/**
	 * 设置主键值
	 * @param key				主键值
	 */
	public abstract void setKey(Object key);

	/**
	 * 生成唯一的rowkey，确保在分布式环境中必须唯一，在关系型数据库中该字段暂不使用
	 * <p>该方法生成的key必须与主键的数据类型相同</p>
	 * @return
	 */
	public Object generateRowKey()
	{
		return DigestUtils.md5Hex(UUID.randomUUID().toString());
	}

	/**
	 * 读取实体的值
	 * @return					值数组
	 */
	public abstract Map<String, Object> getValues();

	/**
	 * 设置实体的值
	 * @param vals				值map,key为identifier.fieldName
	 */
	public abstract void setValues(EntityValue vals);

	/**
	 * 生成代码
	 */
	public void genCode()
	{
		Field[] fs = this.getClass().getDeclaredFields();
		List<Field> fields = new LinkedList<>();
		for (Field it : fs)
		{
			if (it.getAnnotation(ColumnConfig.class) != null)
			{
				fields.add(it);
			}
		}

		// getValues
		System.out.println("Map<String, Object> map = new HashMap<>(" + fields.size() + ");");
		for (Field it : fields)
		{
			System.out.println("map.put(\"" + it.getName() + "\", " + it.getName() + ");");
		}
		System.out.println("return map;");

		// setValues
		System.out.println();
		for (Field it : fields)
		{
			if (it.getType() == int.class)
			{
				System.out.println("this." + it.getName() + " = vals.getInt(\"" + it.getName() + "\");");
			}
			else if (it.getType() == long.class)
			{
				System.out.println("this." + it.getName() + " = vals.getLong(\"" + it.getName() + "\");");
			}
			else if (it.getType() == float.class)
			{
				System.out.println("this." + it.getName() + " = vals.getFloat(\"" + it.getName() + "\");");
			}
			else if (it.getType() == byte.class)
			{
				System.out.println("this." + it.getName() + " = vals.getByte(\"" + it.getName() + "\");");
			}
			else if (it.getType() == String.class)
			{
				System.out.println("this." + it.getName() + " = vals.getString(\"" + it.getName() + "\");");
			}
			else if (it.getType() == char.class)
			{
				System.out.println("this." + it.getName() + " = vals.getChar(\"" + it.getName() + "\");");
			}
			else if (it.getType() == double.class)
			{
				System.out.println("this." + it.getName() + " = vals.getDouble(\"" + it.getName() + "\");");
			}
		}

		// sql table
		System.out.println();
		EntityConfig ec = this.getClass().getAnnotation(EntityConfig.class);
		System.out.println("create table " + ec.tableName());
		System.out.println("(");
		for (Field it : fields)
		{
			if (it.getType() == int.class)
			{
				System.out.println("    " + it.getName() + "   " + "int   comment \"" + it.getAnnotation(ColumnConfig.class).descr() + "\",");
			}
			else if (it.getType() == long.class)
			{
				System.out.println("    " + it.getName() + "   " + "bigint   comment \"" + it.getAnnotation(ColumnConfig.class).descr() + "\",");
			}
			else if (it.getType() == float.class)
			{
				System.out.println("    " + it.getName() + "   " + "float   comment \"" + it.getAnnotation(ColumnConfig.class).descr() + "\",");
			}
			else if (it.getType() == byte.class)
			{
				System.out.println("    " + it.getName() + "   " + "tinyint   comment \"" + it.getAnnotation(ColumnConfig.class).descr() + "\",");
			}
			else if (it.getType() == String.class)
			{
				System.out
						.println("    " + it.getName() + "   " + "varchar(255)   comment \"" + it.getAnnotation(ColumnConfig.class).descr() + "\",");
			}
			else if (it.getType() == char.class)
			{
				System.out.println("    " + it.getName() + "   " + "char   comment \"" + it.getAnnotation(ColumnConfig.class).descr() + "\",");
			}
			else if (it.getType() == double.class)
			{
				System.out.println("    " + it.getName() + "   " + "double   comment \"" + it.getAnnotation(ColumnConfig.class).descr() + "\",");
			}
		}
		System.out.println("    constraint " + ec.tableName() + "_pk primary key (" + fields.get(0).getName() + ")");
		System.out.println(") engine=myisam comment \"" + ec.descr() + "\";");

	}
}
