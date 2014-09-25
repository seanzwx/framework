package com.sean.ds.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 远程服务配置, 服务接口必须在服务实现的接口列表中的第一个
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServiceConfig
{	
	/**
	 * 服务代理类, 必须实现服务接口且必须提供无参构造函数
	 */
	Class<?> proxy();
	
	/**
	 * 服务配置
	 * @return
	 */
	String config();
}
