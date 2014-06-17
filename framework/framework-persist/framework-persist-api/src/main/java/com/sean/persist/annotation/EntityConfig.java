package com.sean.persist.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sean.persist.core.CachePolicy;
import com.sean.persist.core.DataSourceProvider;
import com.sean.persist.core.DefaultCachePolicy;
import com.sean.persist.core.Partition;

/**
 * 实体配置
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EntityConfig
{
	/**
	 * 映射表名
	 */
	String tableName();

	/**
	 * 数据源
	 */
	Class<? extends DataSourceProvider> dataSource();

	/**
	 * 是否开启缓存，默认false
	 */
	boolean cache() default false;

	/**
	 * 缓存策略，默认为DefaultCachePolicy
	 */
	Class<? extends CachePolicy> cachePolicy() default DefaultCachePolicy.class;

	/**
	 * 分区策略, 默认无分区
	 * @return
	 */
	Class<? extends Partition> partition() default Partition.class;

	/**
	 * 备注
	 */
	String description();
}
