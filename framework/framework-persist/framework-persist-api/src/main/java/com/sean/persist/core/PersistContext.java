package com.sean.persist.core;

import java.util.HashMap;
import java.util.Map;

import com.sean.persist.dictionary.Dictionary;
import com.sean.persist.dictionary.DictionaryManager;

/**
 * 持久化抽象上下文
 * @author sean
 */
@SuppressWarnings("unchecked")
public final class PersistContext
{
	public static PersistContext CTX;
	private EntityDaoManager entityDaoManager;
	private DictionaryManager dynamicDicManager;
	protected Map<String, Partition> partitions;
	protected Map<String, CachePolicy> cachePolicys;

	protected PersistContext()
	{
		partitions = new HashMap<>();
		cachePolicys = new HashMap<>();
	}

	protected void setComponent(EntityDaoManager entityDaoManager, DictionaryManager dynamicDicManager)
	{
		this.entityDaoManager = entityDaoManager;
		this.dynamicDicManager = dynamicDicManager;
	}

	/**
	 * 读取数据库访问接口
	 * @return
	 */
	protected <E extends Entity> EntityDao<E> getEntityDao(Class<E> entity)
	{
		return (EntityDao<E>) this.entityDaoManager.getEntityDao(entity);
	}

	/**
	 * 获取数据字典
	 * @param dicName						字典名称
	 * @return
	 */
	public Dictionary getDictionary(String dicName)
	{
		return this.dynamicDicManager.getDictionary(dicName);
	}

	/**
	 * 读取分区策略
	 * @param partitionClass
	 * @return
	 */
	public <E> E getPartition(Class<? extends Partition> partitionClass)
	{
		return (E) this.partitions.get(partitionClass.getSimpleName());
	}
	
	/**
	 * 保存分区策略
	 * @param partition
	 */
	public void setPartition(Partition partition)
	{
		this.partitions.put(partition.getClass().getSimpleName(), partition);
	}
}
