package com.sean.unittest.entity;

/**
 * 单元测试用例参数
 * @author Sean
 *
 */
public class Parameter
{
	private String name;
	private String value;
	
	public Parameter(String name,String value)
	{
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public String getValue()
	{
		return value;
	}

}
