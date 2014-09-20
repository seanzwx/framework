package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sean.service.enums.DataType;
import com.sean.service.enums.ParameterType;

/**
 * action parameter
 * @author sean
 */
@Documented 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ParameterConfig
{	
	/**
	 * data type
	 */
	DataType dataType();
	
	/**
	 * parameter type，default by Single
	 */
	ParameterType type() default ParameterType.Single;
	
	/**
	 * description
	 */
	String description();
	
	/**
	 * error msg
	 * @return
	 */
	String errormsg() default "";
	
	/**
	 * regex pattern
	 */
	String regex() default "";
	
	/**
	 * max length of string
	 */
	int length() default 0;
	
	/**
	 * values of enum
	 * @return
	 */
	String[] enumVals() default {};
}
