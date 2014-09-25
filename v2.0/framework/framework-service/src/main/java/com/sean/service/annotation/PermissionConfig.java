package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限配置
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PermissionConfig
{
	/**
	 * 权限描述
	 */
	String description();

	/**
	 * parent permission, default by 0 , that is the top permission
	 */
	int parent() default 0;
}
