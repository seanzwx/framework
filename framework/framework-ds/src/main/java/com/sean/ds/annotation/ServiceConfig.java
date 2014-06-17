package com.sean.ds.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sean.ds.fail.FailOver;
import com.sean.ds.fail.FailStrategy;
import com.sean.ds.route.RandomAccess;
import com.sean.ds.route.RouteStrategy;

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
	 * 路由策略
	 * @return
	 */
	Class<? extends RouteStrategy> route() default RandomAccess.class;
	
	/**
	 * 失败转移策略
	 * @return
	 */
	Class<? extends FailStrategy> fail() default FailOver.class;

	/**
	 * 服务代理类, 必须实现服务接口且必须提供无参构造函数
	 */
	Class<?> proxy();
	
	/**
	 * 地址
	 * @return
	 */
	String hostname() default "localhost";
	
	/**
	 * 服务监听端口
	 */
	int port();

	/**
	 * 服务权重，主要针对带权重的路由策略
	 * @return
	 */
	float weight() default 1.0f;
}
