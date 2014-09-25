package com.sean.common.util;

import java.util.Properties;

import com.sean.common.enums.AppServerType;

/**
 * jndi工具类
 * @author sean
 */
public class JndiUtil
{
	/**
	 * 获取JNDI Properties
	 * @param ip						IP
	 * @param port						端口
	 * @param appServer					应用服务器类型
	 * @return
	 */
	public static Properties getJndiProps(String ip, int port, AppServerType appServer)
	{
		Properties props = new Properties();
		if (appServer == AppServerType.Glassfish)
		{
			props.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
			props.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
			props.setProperty("java.naming.factory.state", "com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
			props.setProperty("org.omg.CORBA.ORBInitialHost", ip);
			props.setProperty("org.omg.CORBA.ORBInitialPort", String.valueOf(port));
		}
		else if (appServer == AppServerType.WebLogic)
		{
			props.setProperty("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
			props.setProperty("java.naming.provider.url", "t3://" + ip + ":" + port);
		}
		return props;
	}

	/**
	 * 获取JNDI名称
	 * @param appName					应用名称
	 * @param beanName					ejb名称
	 * @param appServer					应用服务器类型
	 * @return
	 */
	public static String getJndiName(String appName, String beanName, AppServerType appServer)
	{
		if (appServer == AppServerType.Glassfish)
		{
			return "java:global/" + appName + "/" + beanName;
		}
		else if (appServer == AppServerType.WebLogic)
		{
			return "java:global/" + appName + "/" + beanName;
		}
		return null;
	}
}
