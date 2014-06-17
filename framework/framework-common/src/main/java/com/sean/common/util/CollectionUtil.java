package com.sean.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 集合工具类
 * @author sean
 *
 */
public class CollectionUtil
{
	/**
	 * 数组转集合
	 * @param array						数组
	 * @return
	 */
	public static <E> List<E> arrayToList(E[] array)
	{
		List<E> list = new ArrayList<E>(array.length);
		for (int i = 0; i < array.length; i++)
		{
			list.add(array[i]);
		}
		return list;
	}

	/**
	 * 数组转集合
	 * @param array						数组
	 * @param size						集合大小
	 * @return
	 */
	public static <E> List<E> arrayToList(E[] array, int size)
	{
		List<E> list = new ArrayList<E>(size);
		for (int i = 0; i < array.length; i++)
		{
			list.add(array[i]);
		}
		return list;
	}
	
	public static <E> List<E> mapToList(String[] keys, Map<String, E> map)
	{
		List<E> list = new ArrayList<E>(keys.length);
		for (int i = 0; i < keys.length; i++)
		{
			list.add(map.get(keys[i]));	
		}
		return list;
	}

	/**
	 * 复制Set
	 * @param src
	 * @param dest
	 */
	public static <E> void copySet(Set<E> src, Set<E> dest)
	{
		for (Iterator<E> it = src.iterator(); it.hasNext();)
		{
			dest.add(it.next());
		}
	}
}
