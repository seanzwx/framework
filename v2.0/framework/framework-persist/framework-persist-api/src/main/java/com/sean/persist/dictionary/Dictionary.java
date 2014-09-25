package com.sean.persist.dictionary;

import java.util.Map;

import com.sean.persist.entity.DictionaryEntity;

/**
 * dicitonary
 * @author sean
 */
public abstract class Dictionary
{
	protected DictionaryEntity entity;

	/**
	 * <p>get field value>/p>
	 * <p>注意：一个实体不管定义了多少字典，都会存放在dic参数中，所以，在不同的字典中，不要使用相同的key</p>
	 * @param id						字典检索Id
	 * @param dic						将检索到的数据放入该map中，注意，不要重复的key
	 */
	public abstract void getDicVal(Object id, Map<String, String> dic);

	public void init(DictionaryEntity entity)
	{
		if (this.entity == null)
		{
			this.entity = entity;
		}
	}

	public DictionaryEntity getEntity()
	{
		return entity;
	}
}
