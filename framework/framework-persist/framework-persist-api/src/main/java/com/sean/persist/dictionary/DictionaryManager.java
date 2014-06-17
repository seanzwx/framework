package com.sean.persist.dictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.persist.entity.DictionaryEntity;
import com.sean.persist.enums.L;
import com.sean.persist.parser.DictionaryParser;

/**
 * 动态数据字典管理类
 * @author sean
 */
public class DictionaryManager
{
	private Map<String, Dictionary> dics;// 所有字典
	private static final Logger logger = LogFactory.getLogger(L.Persist);

	public DictionaryManager(List<Class<?>> cls, List<Class<?>> providers) throws Exception
	{
		logger.info("DictionaryManager start initializing...");
		// 初始化字典
		dics = new HashMap<String, Dictionary>();
		// 创建字典
		DictionaryParser parser = new DictionaryParser();
		for (Class<?> c : cls)
		{
			DictionaryEntity dicEntity = parser.parse(c);
			if (dics.get(dicEntity.getName()) != null)
			{
				throw new RuntimeException("the dictionary " + dicEntity.getName() + " defined repeated,please try to define in other class names");
			}
			for (Class<?> p : providers)
			{
				if (c == p.getInterfaces()[0])
				{
					// 创建字典
					Dictionary dic = (Dictionary) p.newInstance();
					dic.init(dicEntity);
					this.dics.put(dicEntity.getName(), dic);
					logger.debug("DictionaryManager load DynamicDicHandler " + dicEntity.getName() + " successfully");
					break;
				}
			}
		}
		logger.info("DictionaryManager initialized successfully");
	}

	/**
	 * 获取动态数据字典处理对象
	 * @param name					动态数据字典名称
	 * @return
	 */
	public Dictionary getDictionary(String name)
	{
		return this.dics.get(name);
	}

}
