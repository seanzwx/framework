package com.sean.unittest.exception;

/**
 * 单元测试套件不合法异常
 * @author Sean
 *
 */
public class TestSuiteIllegalException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private String testSuiteClass;
	
	public TestSuiteIllegalException(String testSuiteClass)
	{
		this.testSuiteClass = testSuiteClass;
	}
	
	@Override
	public String getMessage()
	{
		return "the testsuite " + this.testSuiteClass + " defined illegal,it must be an interface and comment TestSuiteConfig";
	}
}
