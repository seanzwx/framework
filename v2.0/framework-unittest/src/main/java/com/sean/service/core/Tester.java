package com.sean.service.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sean.config.core.Config;
import com.sean.config.enums.ConfigEnum;
import com.sean.service.annotation.ActionConfig;
import com.sean.service.annotation.DescriptConfig;
import com.sean.unittest.annotation.TestBoxConfig;
import com.sean.unittest.core.HttpWriterTesterImpl;
import com.sean.unittest.core.SessionTesterImpl;
import com.sean.unittest.entity.TestCase;
import com.sean.unittest.entity.TestSuite;
import com.sean.unittest.parser.TestSuiteParser;

/**
 * 单元测试器，在单元测试中，Servlet容器只是模拟，在Listener不能使用Servlet容器属性，而且没有数据库事务支持
 * @author Sean
 */
public final class Tester
{
	private TestContext tc;
	private Map<String, Object> httpSession = new HashMap<String, Object>();
	private final RequestChecker checker = new RequestChecker();
	private HttpWriter httpWriter = new HttpWriterTesterImpl();
	private long userId;

	public Tester(TestContext tc)
	{
		this.tc = tc;
	}

	/**
	 * 设置登录用户
	 * @param username 用户名
	 */
	protected void setLoginUser(long userId)
	{
		this.userId = userId;
	}

	/**
	 * 将对象放入session中
	 * @param name
	 * @param val
	 */
	protected void setSessionAttribute(String name, Object val)
	{
		this.httpSession.put(name, val);
	}

	/**
	 * 测试单元测试套件
	 * @param testSuite 单元测试套件的class
	 */
	public void testSuite(final Class<?> testSuite) throws Exception
	{
		System.out.println("start testing...\n");
		long start = System.currentTimeMillis();
		this.test(testSuite);
		long end = System.currentTimeMillis();
		System.out.println("test finished, costs " + (end - start) + " milliseconds");
		tc.destoryed(null);
	}

	/**
	 * 测试单元测试箱
	 * @param testBox 单元测试箱class
	 */
	public void testBox(final Class<?> testBox) throws Exception
	{
		System.out.println("start testing...\n");
		long start = System.currentTimeMillis();

		TestBoxConfig tbc = testBox.getAnnotation(TestBoxConfig.class);

		DescriptConfig descr = testBox.getAnnotation(DescriptConfig.class);
		String txt = descr == null ? "匿名测试包" : descr.value();

		System.out.println("test box --- " + txt);

		Class<?>[] testSuites = tbc.testSuites();
		for (int i = 0; i < testSuites.length; i++)
		{
			if (i == 0)
			{
				System.out.println("=================================================================\n");
			}
			this.test(testSuites[i]);
			System.out.println("=================================================================\n");
		}
		tc.destoryed(null);
		long end = System.currentTimeMillis();
		System.out.println("test finished, costs " + (end - start) + " milliseconds");
	}

	/**
	 * 测试
	 * @param testSuite 单元测试套件的class
	 */
	private void test(final Class<?> testSuite) throws Exception
	{
		// 获取测试套件
		TestSuite ts = new TestSuiteParser().parse(testSuite);
		ActionConfig ac = ts.getActCls().getAnnotation(ActionConfig.class);
		System.out.println("test suite " + ts.getActCls().getSimpleName() + " " + ac.version() + " --- " + ts.getDescription() + "\n");
		List<TestCase> tcs = ts.getTestCases();
		Version version = (Version) ac.version().newInstance();
		Action act = FrameworkContext.CTX.getAction(version.getVersion(), ts.getActCls().getSimpleName());
		if (act != null)
		{
			TestCase tc = null;
			for (int i = 0; i < tcs.size(); i++)
			{
				if (i == 0)
				{
					System.out.println("-----------------------------------------------------------------\n");
				}
				tc = tcs.get(i);
				int times = tc.getTestTimes();
				for (int j = 0; j < times; j++)
				{
					// 打印测试次数
					System.out.println("testcase --- " + tc.getDescription() + " test " + (j + 1) + " time:");
					// 开始测试
					// 定义会话
					Session session = null;
					try
					{
						// 创建Session会话
						session = new SessionTesterImpl(act, httpSession, tc.getParametersMap());
						if (userId != 0)
						{
							session.setLoginInfo(userId);
						}

						// 验证参数合法性
						if (!act.checkParams(session, checker))
						{
							continue;
						}

						// 如果是伪装实现运行模式
						if ("pseudo".equals(Config.getProperty(ConfigEnum.ServiceMode)))
						{
							act.pseudo(session);
							continue;
						}

						FrameworkContext.CTX.getUserInterface().initUserContext(session.getUserId());
						// 执行execute
						act.execute(session);
					}
					catch (BusinessException be)
					{
						session.businessException(be.getMessage(), be.getCode());
					}
					catch (Exception e)
					{
						// 清空所有返回参数，异常标记
						session.clearReturnAttribute();
						session.exception();
						e.printStackTrace();
					}
					finally
					{
						try
						{
							// 写入客户端
							httpWriter.write(null, null, session, act.getActionEntity());
							FrameworkContext.CTX.getUserInterface().destoryUserContext();
							// 打印测试次数
							System.out.println("testcase " + tc.getDescription() + " test finished\n");
							System.out.println("-----------------------------------------------------------------\n");
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
