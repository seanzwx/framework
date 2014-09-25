package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sean.service.core.Module;

/**
 * Action请求参数定义
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ParameterProviderConfig
{
	/**
	 * 注释
	 */
	String description();

	/**
	 * 模块
	 */
	Class<? extends Module> module();
}
