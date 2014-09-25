package com.sean.persist.core;

/**
 * 默认缓存策略，默认的策略返回常量，即刷新全部语句缓存
 * @author sean
 */
public class DefaultCachePolicy extends CachePolicy
{
	@Override
	public String getStatementCacheName(String dbName, String tableName)
	{
		StringBuilder sb = new StringBuilder(dbName.length() + tableName.length() + 12);
		sb.append(dbName).append('_').append(tableName).append('_').append("statement");
		return sb.toString();
	}
}
