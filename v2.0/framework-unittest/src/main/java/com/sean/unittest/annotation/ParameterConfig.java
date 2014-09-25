package com.sean.unittest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>单元测试用例参数</p>
 * @author Sean
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ParameterArrayConfig.class)
public @interface ParameterConfig
{
	/**
	 * <p>参数名</p>
	 */
	String name();

	/**
	 * <p>参数值</p>
	 * <p>如果value和values一起设置，默认values优先级高</p>
	 */
	String value() default "";

	/**
	 * <p>批量参数</p>
	 * <p>如果value和values一起设置，默认values优先级高</p>
	 */
	String[] values() default {};
}