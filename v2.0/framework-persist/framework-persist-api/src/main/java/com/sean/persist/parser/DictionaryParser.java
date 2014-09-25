package com.sean.persist.parser;

import com.sean.persist.dictionary.DictionaryConfig;
import com.sean.persist.entity.DictionaryEntity;

/**
 * 数据字典解析器
 * @author sean
 *
 */
public class DictionaryParser
{
	public DictionaryEntity parse(Class<?> cls)
	{
		DictionaryConfig dc = cls.getAnnotation(DictionaryConfig.class);
		DictionaryEntity dic = new DictionaryEntity(cls.getName(), dc.description(), dc.keys(), cls);
		return dic;
	}
}
