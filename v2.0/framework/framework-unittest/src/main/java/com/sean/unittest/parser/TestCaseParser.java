package com.sean.unittest.parser;

import java.lang.reflect.Method;

import com.sean.unittest.annotation.ParameterConfig;
import com.sean.unittest.annotation.TestCaseConfig;
import com.sean.unittest.entity.Parameter;
import com.sean.unittest.entity.TestCase;
import com.sean.unittest.exception.TestCaseIllegalException;

/**
 * 单元测试用力解析
 * @author Sean
 */
public class TestCaseParser
{
	/**
	 * 转换测试套件
	 * @param teseCase				测试用例
	 * @return
	 */
	public TestCase parse(Method teseCase) throws Exception
	{
		TestCaseConfig tcc = teseCase.getAnnotation(TestCaseConfig.class);
		if (tcc == null)
		{
			throw new TestCaseIllegalException(teseCase.getName());
		}

		TestCase tc = new TestCase(tcc.testTimes(), tcc.description());
		ParameterConfig[] pcs = tcc.parameters();
		ParameterConfig pc = null;
		Parameter param = null;
		for (int i = 0; i < pcs.length; i++)
		{
			pc = pcs[i];
			// 如果是批量参数
			if (pc.values().length > 0)
			{
				String[] values = pc.values();
				for (int j = 0; j < values.length; j++)
				{
					param = new Parameter(pc.name(), values[j]);
					tc.addParameter(param);
				}
			}
			// 单一参数
			else
			{
				param = new Parameter(pc.name(), pc.value());
				tc.addParameter(param);
			}
		}
		return tc;
	}
}
