package com.sean.service.entity;

import com.sean.service.enums.Format;
import com.sean.service.writer.FieldWriter;

/**
 * Pseudo实体
 * @author sean
 *
 */
public class ReturnParameterEntity
{
	private String name;
	private Format format;
	private Class<?> entity;
	private String[] fields;
	private UseDicEntity[] dics;
	private String description;
	private FieldWriter fieldWriter;

	public ReturnParameterEntity(String name, Format format, Class<?> entity, String[] fields, UseDicEntity[] dics, String description,
			FieldWriter fieldWriter)
	{
		this.name = name;
		this.format = format;
		this.entity = entity;
		this.fields = fields;
		this.dics = dics;
		this.description = description;
		this.fieldWriter = fieldWriter;
	}

	public String getName()
	{
		return name;
	}

	public Class<?> getEntity()
	{
		return entity;
	}

	public String[] getFields()
	{
		return fields;
	}

	public String getDescription()
	{
		return description;
	}

	public UseDicEntity[] getDics()
	{
		return dics;
	}

	public Format getFormat()
	{
		return format;
	}

	public FieldWriter getFieldWriter()
	{
		return fieldWriter;
	}

}
