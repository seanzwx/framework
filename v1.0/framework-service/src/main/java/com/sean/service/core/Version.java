package com.sean.service.core;

/**
 * 版本控制
 * @author sean
 */
public interface Version
{
	/**
	 * 获取版本名称
	 * @return
	 */
	public String getVersion();

	/**
	 * 获取版本序号
	 * @return
	 */
	public int getVersionIndex();
}
