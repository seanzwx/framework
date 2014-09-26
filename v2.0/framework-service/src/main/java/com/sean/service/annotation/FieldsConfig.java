package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实体属性列表，用于输出json
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FieldsConfig
{
	/**
	 * 输出的字段, 若为*, 则长度之能为1
	 */
	String[] value();

	/**
	 * 过滤字段, 当且仅当value = *情况下奏效
	 */
	String[] exclude() default {};
}
