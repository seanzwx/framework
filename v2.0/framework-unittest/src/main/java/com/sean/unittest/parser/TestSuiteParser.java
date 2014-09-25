package com.sean.unittest.parser;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.sean.service.annotation.DescriptConfig;
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
		
		DescriptConfig descr = testSuiteClass.getAnnotation(DescriptConfig.class);
		String txt = descr == null ? "匿名测试套件" : descr.value();
		
		TestSuite ts = new TestSuite(tsc.action(), txt);

		TestCaseParser parser = new TestCaseParser();

		// 读取测试用例
		Method[] ms = testSuiteClass.getDeclaredMethods();
		List<Method> list = new LinkedList<>();
		for (Method it : ms)
		{
			list.add(it);
		}
		// 根据方法名称排序
		Collections.sort(list, new Comparator<Method>()
		{
			@Override
			public int compare(Method o1, Method o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});

		for (Method it : list)
		{
			ts.addTestCase(parser.parse(it));
		}
		return ts;
	}
}
