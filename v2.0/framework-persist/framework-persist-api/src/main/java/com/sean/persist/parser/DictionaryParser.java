package com.sean.persist.parser;

import com.sean.persist.dictionary.DictionaryConfig;
import com.sean.persist.dictionary.DictionaryKeyConfig;
import com.sean.persist.entity.DictionaryEntity;

/**
 * 数据字典解析器
 * @author sean
 */
public class DictionaryParser
{
	public DictionaryEntity parse(Class<?> cls)
	{
		DictionaryKeyConfig[] keys = cls.getAnnotationsByType(DictionaryKeyConfig.class);
		DictionaryConfig dc = cls.getAnnotation(DictionaryConfig.class);
		DictionaryEntity dic = new DictionaryEntity(cls.getName(), dc.value(), keys, cls);
		return dic;
	}
}
