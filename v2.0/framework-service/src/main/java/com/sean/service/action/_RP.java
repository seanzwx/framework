package com.sean.service.action;

import com.sean.service.annotation.ReturnParameterConfig;
import com.sean.service.annotation.ReturnParameterProviderConfig;
import com.sean.service.enums.Format;

@ReturnParameterProviderConfig(module = _M.class, descr = "框架内置接口返回参数列表")
public class _RP
{
	@ReturnParameterConfig(descr = "api列表", format = Format.Json)
	public static final String apiList = "apiList";

	@ReturnParameterConfig(descr = "api接口", format = Format.Json)
	public static final String api = "api";
}
