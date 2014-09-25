package com.sean.unittest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>单元测试箱，可以批量测试多个接口</p>
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestBoxConfig
{
	/**
	 * <p>单元测试箱描述，无实际意义</p>
	 */
	String description();

	/**
	 * <p>单元测试套件类数组</p>
	 */
	Class<?>[] testSuites() default {};
}
