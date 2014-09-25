package com.sean.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 说明
 * @author sean
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface DescriptConfig
{
	String value();
}
