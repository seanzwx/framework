package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sean.service.core.Module;
import com.sean.service.core.PermissionProvider;
import com.sean.service.core.V1;
import com.sean.service.core.Version;
import com.sean.service.enums.ReturnType;

/**
 * 请求Action配置
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ActionConfig
{
	/**
	 * 是否需要事务，默认false
	 */
	boolean transaction() default false;

	/**
	 * 权限ID，默认是0，即没有权限
	 */
	int permission() default PermissionProvider.None;

	/**
	 * 是否开启认证，默认true
	 */
	boolean authenticate() default true;

	/**
	 * 返回参数类型
	 */
	ReturnType returnType() default ReturnType.Json;

	/**
	 * 模块
	 */
	Class<? extends Module> module();

	/**
	 * 版本
	 */
	Class<? extends Version> version() default V1.class;

	/**
	 * 接口密码, 访问该接口必须在请求参数加password参数验证, 当且仅当参数于该值一致才可通过验证, 否则返回Denied
	 */
	String password() default "";
}
