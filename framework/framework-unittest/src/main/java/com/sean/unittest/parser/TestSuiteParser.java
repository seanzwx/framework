package com.sean.unittest.parser;

import java.lang.reflect.Method;

import com.sean.service.core.Action;
import com.sean.unittest.annotation.TestSuiteConfig;
import com.sean.unittest.entity.TestSuite;
import com.sean.unittest.exception.TestSuiteActionException;
import com.sean.unittest.exception.TestSuiteIllegalException;

/**
 * 单元测试套件转换器
 * @author Sean
 */
public class TestSuiteParser
{
	/**
	 * 转换测试套件
	 * @param testSuiteClass				测试套件Class
	 * @return
	 */
	public TestSuite parse(Class<?> testSuiteClass) throws Exception
	{
		// 读取测试套件
		TestSuiteConfig tsc = testSuiteClass.getAnnotation(TestSuiteConfig.class);
		if (!testSuiteClass.isInterface() || tsc == null)
		{
			throw new TestSuiteIllegalException(testSuiteClass.getName());
		}
		if (tsc.action().getSuperclass() != Action.class)
		{
			throw new TestSuiteActionException(testSuiteClass.getName(), tsc.action());
		}
		TestSuite ts = new TestSuite(tsc.action(), tsc.description());

		TestCaseParser parser = new TestCaseParser();

		// 读取测试用例
		Method[] ms = testSuiteClass.getDeclaredMethods();
		for (int i = 0; i < ms.length; i++)
		{
			ts.addTestCase(parser.parse(ms[i]));
		}
		return ts;
	}
}
