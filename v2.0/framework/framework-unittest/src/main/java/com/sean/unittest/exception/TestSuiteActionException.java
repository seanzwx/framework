package com.sean.unittest.exception;

/**
 * 单元测试套件不合法异常
 * @author Sean
 *
 */
public class TestSuiteActionException extends Exception
{
	private static final long serialVersionUID = 1L;

	private String testSuiteClass;
	private Class<?> action;

	public TestSuiteActionException(String testSuiteClass, Class<?> action)
	{
		this.action = action;
		this.testSuiteClass = testSuiteClass;
	}

	@Override
	public String getMessage()
	{
		return "the test action " + action.getName() + " in testsuite " + this.testSuiteClass + " is not a action";
	}
}
