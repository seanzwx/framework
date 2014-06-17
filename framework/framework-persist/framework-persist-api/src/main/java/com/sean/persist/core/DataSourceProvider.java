package com.sean.persist.core;

import com.sean.persist.entity.DataSourceEntity;

public abstract class DataSourceProvider
{
	/**
	 * 提供数据源
	 * @return
	 */
	public abstract DataSourceEntity providerDs();
	
	/**
	 * 提供单元测试数据源
	 * @return
	 */
	public abstract DataSourceEntity providerUnitTestDs();
}
