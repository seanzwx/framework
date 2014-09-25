package com.sean.index.config;

import org.apache.lucene.util.Version;

import com.sean.config.core.Config;
import com.sean.config.enums.ConfigEnum;

/**
 * 全局配置文件
 * @author sean
 */
public class IndexConfig
{
	/**
	 * Lucene版本
	 */
	public static final Version VERSION = Version.LUCENE_46;

	/**
	 * 文档隐藏域名称
	 */
	public static final String KEY = "_id";

	/**
	 * 默认内存索引大小
	 */
	public static int DEFAULT_RAM_SIZE = 8;

	/**
	 * 默认过时列表长度
	 */
	public static int DEFAULT_STALE_SIZE = 1024;

	/**
	 * HDFS地址
	 */
	public static String HDFS = "hdfs://localhost:9000";

	static
	{
		DEFAULT_RAM_SIZE = Integer.parseInt(Config.getProperty(ConfigEnum.IndexRamSizeMB));
		DEFAULT_STALE_SIZE = Integer.parseInt(Config.getProperty(ConfigEnum.IndexStaleSize));
	}
}
