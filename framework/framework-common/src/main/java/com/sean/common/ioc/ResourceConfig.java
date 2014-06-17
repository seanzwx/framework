package com.sean.common.ioc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * inject bean to specify field, this annotation must be used in the class which is a bean(@BeanConfig).
 * @author Sean
 */
@Documented 
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResourceConfig
{
}
