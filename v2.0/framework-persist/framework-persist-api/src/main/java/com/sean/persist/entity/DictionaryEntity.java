package com.sean.persist.entity;

import com.sean.persist.dictionary.DictionaryKeyConfig;

/**
 * 字典实体
 * @author sean
 *
 */
public class DictionaryEntity
{
	private String name;
	private String description;
	private DictionaryKeyEntity[] keys;
	private Class<?> cls;

	public DictionaryEntity(String name, String description, DictionaryKeyConfig[] keys, Class<?> cls)
	{
		this.name = name;
		this.description = description;
		this.cls = cls;

		this.keys = new DictionaryKeyEntity[keys.length];
		for (int i = 0; i < keys.length; i++)
		{
			this.keys[i] = new DictionaryKeyEntity(keys[i].key(), keys[i].descr());
		}
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public DictionaryKeyEntity[] getKeys()
	{
		return keys;
	}

	public Class<?> getCls()
	{
		return cls;
	}

}
