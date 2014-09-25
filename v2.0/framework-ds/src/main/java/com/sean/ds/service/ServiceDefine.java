package com.sean.ds.service;

import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * 服务定义
 * @author sean
 */
public class ServiceDefine
{
	public String serviceName;
	public String serviceClass;
	public String routeClass;
	public String failClass;
	public String proxyClass;
	public int failRetries;

	private Class<?> serviceClazz;
	private Class<?> proxyClazz;
	private Class<?> routeClazz;
	private Class<?> failClazz;

	public ServiceDefine()
	{
	}

	public ServiceDefine(Class<?> serviceClass, Class<?> proxyClass, Class<?> routeClass, Class<?> failClass, int failRetries)
	{
		this.serviceName = serviceClass.getSimpleName();
		this.serviceClass = serviceClass.getName();
		this.proxyClass = proxyClass.getName();
		this.routeClass = routeClass.getName();
		this.failClass = failClass.getName();
		this.failRetries = failRetries;
	}

	protected Class<?> getServiceClass()
	{
		return this.serviceClazz;
	}

	protected Class<?> getProxyClass()
	{
		return this.proxyClazz;
	}

	protected Class<?> getRouteClass()
	{
		return this.routeClazz;
	}

	protected Class<?> getFailClass()
	{
		return this.failClazz;
	}

	public int getFailRetries()
	{
		return failRetries;
	}

	/**
	 * 反射类
	 */
	protected void reflectClass()
	{
		if (serviceClazz == null)
		{
			try
			{
				this.serviceClazz = Class.forName(this.serviceClass);
				this.proxyClazz = Class.forName(this.proxyClass);
				this.routeClazz = Class.forName(this.routeClass);
				this.failClazz = Class.forName(this.failClass);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取服务实例路径
	 * @param instance
	 * @return
	 */
	protected String getInstancePath(ServiceInstance instance)
	{
		StringBuilder sb = new StringBuilder(128);
		sb.append("/services/").append(serviceName).append("/").append(instance.hostname).append(':').append(String.valueOf(instance.port));
		return sb.toString();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(128);
		sb.append("serviceName:").append(serviceName).append(", serviceClass:").append(serviceClass);
		return sb.toString();
	}

	public String toJson()
	{
		Map<String, String> json = new LinkedHashMap<>();
		json.put("serviceName", serviceName);
		json.put("serviceClass", serviceClass);
		json.put("proxyClass", proxyClass);
		json.put("routeClass", routeClass);
		json.put("failClass", failClass);
		json.put("failRetries", String.valueOf(failRetries));
		return JSON.toJSONString(json);
	}
}
