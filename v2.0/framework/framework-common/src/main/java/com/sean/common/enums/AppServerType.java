package com.sean.common.enums;

/**
 * 应用服务器类型
 * @author Sean
 *
 */
public enum AppServerType
{
	/**
	 * Oracle Glassfish
	 */
	Glassfish("java:comp/UserTransaction"),

	/**
	 * Oracle WebLogic
	 */
	WebLogic("java:comp/UserTransaction"),

	/**
	 * Resin 
	 */
	Resin("java:comp/UserTransaction"),

	/**
	 * Jboss-eap/Jboss7
	 */
	Jboss("java:jboss/UserTransaction");

	private String userTransaction;

	AppServerType(String userTransaction)
	{
		this.userTransaction = userTransaction;
	}

	public String getUserTranaction()
	{
		return this.userTransaction;
	}
}
