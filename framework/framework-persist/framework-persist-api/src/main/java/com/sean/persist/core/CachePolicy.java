package com.sean.persist.core;

/**
 * 缓存策略接口
 * @author sean
 */
public abstract class CachePolicy
{
	/**
	 * 读取查询语句缓存名称，该名称涉及到刷新语句缓存策略，如果是每次有写操作就全部刷新，返回常量字符串既可以，如果每次有写操作只是刷新局部缓存，
	 * 则需要在每个部分的缓存加上特定标识
	 * 
	 * eg1：数据按照companyId水平分割，互不影响，因此该方法可以返回statement_${companyId}，companyId为具体的值, 
	 * 因此当刷新缓存的时候，也只刷新该company的缓存，提高效率。
	 * @return
	 */
	public abstract String getStatementCacheName(String dbName, String tableName);
}
