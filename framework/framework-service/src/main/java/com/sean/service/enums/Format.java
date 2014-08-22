package com.sean.service.enums;

/**
 * return parameter format
 * @author sean
 */
public enum Format
{
	/**
	 * numeric
	 */
	Numeric("数值"),

	/**
	 * string
	 */
	String("字符串"),

	/**
	 * json
	 */
	Json("Json"),

	/**
	 * map
	 */
	Map("Map"),
	
	/**
	 * 表格
	 */
	Table("Table"),

	/**
	 * entity
	 */
	Entity("实体"),

	/**
	 * entity list
	 */
	EntityList("实体列表"),

	/**
	 * avro entity
	 */
	AvroEntity("avro实体"),

	/**
	 * avro entity list
	 */
	AvroEntityList("avro实体列表");

	private String code;

	Format(String code)
	{
		this.code = code;
	}

	public String getValue()
	{
		return code;
	}
}
