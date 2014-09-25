package com.sean.persist.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sean.common.util.ClassScanner;
import com.sean.persist.annotation.EntityConfig;
import com.sean.persist.dictionary.Dictionary;
import com.sean.persist.dictionary.DictionaryConfig;
import com.sean.persist.dictionary.DictionaryProviderConfig;

/**
 * 类加载器
 * @author Sean
 */
public final class ClassCollector
{
	private String[] pkgPrefixs;

	/**
	 * 包名前缀
	 * @param pkgPrefix
	 */
	public ClassCollector(String[] pkgPrefixs)
	{
		this.pkgPrefixs = pkgPrefixs;
	}

	/**
	 * 扫描所有类
	 * @return
	 */
	public Map<String, List<Class<?>>> collect() throws Exception
	{
		List<Class<?>> dicProviders = new ArrayList<Class<?>>(5);
		List<Class<?>> entitys = new ArrayList<Class<?>>(50);
		List<Class<?>> dicHandlers = new ArrayList<Class<?>>(10);
		Map<String, List<Class<?>>> classes = new HashMap<String, List<Class<?>>>();
		classes.put("dictionaryProviders", dicProviders);
		classes.put("entitys", entitys);
		classes.put("dictionarys", dicHandlers);
		this.list(classes);
		return classes;
	}

	/**
	 * 列出所有注解类
	 * @param classes
	 */
	private void list(Map<String, List<Class<?>>> classes) throws Exception
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		ClassScanner scanner = new ClassScanner();
		List<String> clsStrs = scanner.findClass(loader, pkgPrefixs);
		int length = clsStrs.size();
		Class<?> cls = null;
		for (int i = 0; i < length; i++)
		{
			cls = loader.loadClass(clsStrs.get(i));

			if (cls != null)
			{
				// 如果是实体Entity
				if (cls.getAnnotation(EntityConfig.class) != null)
				{
					if (cls.getSuperclass() == Entity.class)
					{
						classes.get("entitys").add(cls);
					}
					else
					{
						throw new RuntimeException("the entity " + cls.getName() + " defined illegal,it must extends Entity and comment EntityConfig");
					}
				}
				// 如果是动态数据字典定义类
				else if (cls.getAnnotation(DictionaryConfig.class) != null)
				{
					classes.get("dictionarys").add(cls);
				}
				// 如果是动态数据字典处理类
				else if (cls.getAnnotation(DictionaryProviderConfig.class) != null)
				{
					if (cls.getSuperclass() == Dictionary.class)
					{
						classes.get("dictionaryProviders").add(cls);
					}
					// DynamicDic定义不合法
					else
					{
						throw new RuntimeException("the dictionary provider " + cls.getName()
								+ " defined illegal,it must extends Dictionary and comment DictionaryProviderConfig");
					}
				}
			}
		}
	}
}
