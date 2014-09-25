package com.sean.unittest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sean.service.core.Action;

/**
 * <p>单元测试套件，用于测试单一接口，可以在套件中定义多个测试用例</p>
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TestSuiteConfig
{
	/**
	 * <p>测试的action</p>
	 */
	Class<? extends Action> action();
}
