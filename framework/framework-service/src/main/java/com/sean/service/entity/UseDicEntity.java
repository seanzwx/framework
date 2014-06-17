package com.sean.service.entity;

public class UseDicEntity
{
	private String field;
	private String dic;

	public UseDicEntity(String field, String dic)
	{
		this.field = field;
		this.dic = dic;
	}

	public String getField()
	{
		return field;
	}

	public String getDic()
	{
		return dic;
	}

}
