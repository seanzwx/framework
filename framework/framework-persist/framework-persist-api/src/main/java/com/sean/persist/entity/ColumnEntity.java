package com.sean.persist.entity;

import java.lang.reflect.Field;

/**
 * column实体
 * @author sean
 */
public class ColumnEntity
{
	private String column;
	private boolean primaryKey;
	private String sequenceName;
	private String description;
	private Field field;

	public ColumnEntity(String column, boolean primaryKey, String sequenceName, String description, Field field)
	{
		this.column = column;
		this.primaryKey = primaryKey;
		this.sequenceName = sequenceName;
		this.description = description;
		this.field = field;
	}

	public String getColumn()
	{
		return column;
	}

	public boolean isPrimaryKey()
	{
		return primaryKey;
	}

	public String getSequenceName()
	{
		return sequenceName;
	}

	public String getDescription()
	{
		return description;
	}

	public Field getField()
	{
		return field;
	}

}
