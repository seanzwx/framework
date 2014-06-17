package com.sean.unittest.exception;

/**
 * 单元测试用例不合法异常
 * @author Sean
 *
 */
public class TestCaseIllegalException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private String caseName;
	
	public TestCaseIllegalException(String caseName)
	{
		this.caseName = caseName;
	}
	
	@Override
	public String getMessage()
	{
		return "the testcase " + this.caseName + " defined illegal,it must comment TestCaseConfig";
	}
}
