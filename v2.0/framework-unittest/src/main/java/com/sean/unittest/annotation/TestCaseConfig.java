package com.sean.unittest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单元测试用例
 * @author Sean
 *
 */
@Documented 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestCaseConfig
{	
	/**
	 * <p>运行次数，默认是1</p>
	 */
	int testTimes() default 1;
}
