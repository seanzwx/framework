package com.sean.unittest.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 单元 测试用例
 * @author Sean
 * 
 */
public class TestCase
{
	private int testTimes;
	private String description;
	private List<Parameter> parameters;

	/**
	 * 测试用例构造
	 * @param testTimes					测试次数
	 * @param description				用例描述
	 */
	public TestCase(int testTimes, String description)
	{
		this.testTimes = testTimes;
		this.description = description;
		this.parameters = new ArrayList<Parameter>();
	}

	/**
	 * 读取测试次数
	 * @return
	 */
	public int getTestTimes()
	{
		return testTimes;
	}

	/**
	 * 读取用例描述
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * 添加用例参数
	 * @param param						参数
	 */
	public void addParameter(Parameter param)
	{
		this.parameters.add(param);
	}

	/**
	 * 读取用例参数
	 */
	public List<Parameter> getParameters()
	{
		return this.parameters;
	}
	
	/**
	 * 读取参数Map
	 * @return
	 */
	public Map<String,String[]> getParametersMap()
	{
		Map<String, String[]> params = new HashMap<String, String[]>();
		Parameter param = null;
		for (int i = 0; i < this.parameters.size(); i++)
		{
			param = parameters.get(i);
			// 如果已经重复
			String[] tmp = params.get(param.getName());
			if (tmp != null)
			{
				String[] p = new String[tmp.length + 1];
				int j;
				for (j = 0; j < tmp.length; j++)
				{
					p[j] = tmp[j];
				}
				p[j] = param.getValue();
				params.put(param.getName(), p);
			}
			else
			{
				String[] p = new String[1];
				p[0] = param.getValue();
				params.put(param.getName(), p);
			}
		}
		return params;
	}
}
