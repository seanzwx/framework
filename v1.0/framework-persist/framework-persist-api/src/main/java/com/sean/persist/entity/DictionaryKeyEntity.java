package com.sean.persist.entity;

/**
 * 字典键实体
 * @author sean
 *
 */
public class DictionaryKeyEntity
{
	private String key;
	private String description;

	public DictionaryKeyEntity(String key, String description)
	{
		this.key = key;
		this.description = description;
	}

	public String getKey()
	{
		return key;
	}

	public String getDescription()
	{
		return description;
	}

}
