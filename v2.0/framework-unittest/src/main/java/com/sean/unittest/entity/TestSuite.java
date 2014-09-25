package com.sean.unittest.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 单元测试套件
 * @author Sean
 */
public class TestSuite
{
	private Class<?> actCls;
	private String description;
	private List<TestCase> testCases;

	/**
	 * 单元测试套件构造方法
	 * @param actionName			测试Action名称
	 */
	public TestSuite(Class<?> actCls, String description)
	{
		this.actCls = actCls;
		this.description = description;
		this.testCases = new ArrayList<TestCase>();
	}

	public Class<?> getActCls()
	{
		return actCls;
	}

	public String getDescription()
	{
		return description;
	}

	/**
	 * 添加测试用例
	 * @param testCase				测试用例
	 */
	public void addTestCase(TestCase testCase)
	{
		testCases.add(testCase);
	}

	/**
	 * 读取测试用例
	 * @return
	 */
	public List<TestCase> getTestCases()
	{
		return this.testCases;
	}
}
