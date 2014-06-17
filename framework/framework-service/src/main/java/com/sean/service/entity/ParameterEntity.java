package com.sean.service.entity;

import com.sean.service.enums.DataType;
import com.sean.service.enums.ParameterType;

/**
 * Parameter实体
 * @author sean
 *
 */
public class ParameterEntity
{
	private String name;
	private DataType dataType;
	private ParameterType type;
	private String regex;
	private int length;
	private String description;

	public ParameterEntity(String name, DataType dataType, ParameterType type, String regex, int length, String description)
	{
		this.name = name;
		this.dataType = dataType;
		this.type = type;
		this.regex = regex;
		this.length = length;
		this.description = description;
	}

	public String getName()
	{
		return name;
	}

	public DataType getDataType()
	{
		return dataType;
	}

	public ParameterType getType()
	{
		return type;
	}

	public String getRegex()
	{
		return regex;
	}

	public String getDescription()
	{
		return description;
	}

	public int getLength()
	{
		return length;
	}

}
