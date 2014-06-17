package com.sean.index.core;

import java.util.HashMap;
import java.util.Map;

/**
 * IndexDao管理类
 * @author sean
 *
 */
public class IndexDaoManager
{
	private static IndexDaoManager instance = new IndexDaoManager();
	private Map<String, IndexDao> daos;

	private IndexDaoManager()
	{
		daos = new HashMap<String, IndexDao>();
	}

	public static IndexDaoManager getInstance()
	{
		return instance;
	}

	/**
	 * 获取IndexDao
	 * @param path									索引路径
	 * @param keyField								索引主键域
	 * @param backupReader							备份恢复Reader
	 */
	public synchronized IndexDao getIndexDao(String path, String keyField, BackupReader backupReader) throws Exception
	{
		IndexDao dao = daos.get(path);
		if (dao == null)
		{
			dao = new IndexDao(path, keyField, backupReader);
		}
		return dao;
	}

	/**
	 * 获取IndexDao
	 * @param path									索引路径
	 * @param keyField								索引主键域
	 * @param ramSizeMB								内存索引大小
	 * @param staleSize								过时文档列表大小
	 * @param backupReader							备份恢复Reader
	 */
	public synchronized IndexDao getIndexDao(String path, String keyField, int ramSizeMB, int staleSize, BackupReader backupReader) throws Exception
	{
		IndexDao dao = daos.get(path);
		if (dao == null)
		{
			dao = new IndexDao(path, keyField, ramSizeMB, staleSize, backupReader);
		}
		return dao;
	}
}
