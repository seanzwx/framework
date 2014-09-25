package com.sean.service.action;

import com.sean.service.annotation.ParameterConfig;
import com.sean.service.annotation.ParameterProviderConfig;
import com.sean.service.enums.DataType;

/**
 * 框架内置参数，外部不得使用
 * @author sean
 */
@ParameterProviderConfig(module = _M.class, descr = "框架内置接口参数")
public class _P
{
	@ParameterConfig(dataType = DataType.String, descr = "需要加载的css文件路径，多个文件用逗号隔开，路径需要从项目根路径开始") 
	public static final String css = "css";
	@ParameterConfig(dataType = DataType.String, descr = "需要加载的javascript文件路径，多个文件用逗号隔开，路径需要从项目根路径开始") 
	public static final String js = "js";
	@ParameterConfig(dataType = DataType.String, descr = "接口名称") 
	public static final String action = "action";
	@ParameterConfig(dataType = DataType.String, descr = "版本") 
	public static final String version = "version";
}
