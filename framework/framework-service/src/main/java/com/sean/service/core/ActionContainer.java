package com.sean.service.core;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.common.ioc.BeanFactory;
import com.sean.common.ioc.ResourceConfig;
import com.sean.log.core.LogFactory;
import com.sean.service.action.LoadCssAction;
import com.sean.service.action.LoadJsAction;
import com.sean.service.action._P;
import com.sean.service.action._RP;
import com.sean.service.annotation.ParameterConfig;
import com.sean.service.annotation.ParameterProviderConfig;
import com.sean.service.annotation.ReturnParameterConfig;
import com.sean.service.annotation.ReturnParameterProviderConfig;
import com.sean.service.entity.ActionEntity;
import com.sean.service.entity.ParameterEntity;
import com.sean.service.entity.ReturnParameterEntity;
import com.sean.service.enums.L;
import com.sean.service.parser.ActionParser;
import com.sean.service.parser.ParameterParser;
import com.sean.service.parser.ReturnParameterParser;

/**
 * Action容器
 * @author sean
 */
public final class ActionContainer
{
	private Map<String, List<Action>> container = new HashMap<String, List<Action>>();
	private Map<String, Integer> versions = new HashMap<String, Integer>();

	private static Logger logger = LogFactory.getLogger(L.Service);

	public ActionContainer(List<Class<?>> actCls, List<Class<?>> paramCls, List<Class<?>> retParamCls, FrameworkSpi userInterface,
			InterceptorInvoker interceptorInvoker) throws Exception
	{
		// 日志
		logger.info("ActionContainer start initializing...");

		// 获取请求参数,加入框架内置参数
		paramCls.add(0, _P.class);
		ParameterParser paramParser = new ParameterParser();
		Map<String, Map<String, ParameterEntity>> paramMap = new HashMap<>();

		for (int i = 0; i < paramCls.size(); i++)
		{
			Object obj = paramCls.get(i).newInstance();
			Field[] fs = paramCls.get(i).getDeclaredFields();
			Field f = null;

			Map<String, ParameterEntity> params = new HashMap<>();
			for (int j = 0; j < fs.length; j++)
			{
				f = fs[j];
				f.setAccessible(true);
				ParameterConfig pc = f.getAnnotation(ParameterConfig.class);
				if (pc != null)
				{
					ParameterEntity param = paramParser.parse(pc, f.get(obj).toString());
					if (params.get(param.getName()) == null)
					{
						params.put(param.getName(), param);
					}
					else
					{
						throw new RuntimeException("the request parameter " + f.getName() + " defined repeated in " + paramCls.get(i).getName()
								+ ", try in other names");
					}
				}
			}

			ParameterProviderConfig ppc = paramCls.get(i).getAnnotation(ParameterProviderConfig.class);
			paramMap.put(ppc.module().newInstance().getName(), params);
		}

		// 获取返回参数,加入框架内置返回参数
		retParamCls.add(0, _RP.class);
		ReturnParameterParser returnParamParser = new ReturnParameterParser();
		Map<String, Map<String, ReturnParameterEntity>> retparamMap = new HashMap<>();

		for (int i = 0; i < retParamCls.size(); i++)
		{
			Object obj = retParamCls.get(i).newInstance();
			Field[] fs = retParamCls.get(i).getDeclaredFields();
			Field f = null;
			Map<String, ReturnParameterEntity> retparams = new HashMap<>();
			for (int j = 0; j < fs.length; j++)
			{
				f = fs[j];
				f.setAccessible(true);
				ReturnParameterConfig rpc = f.getAnnotation(ReturnParameterConfig.class);
				if (rpc != null)
				{
					ReturnParameterEntity param = returnParamParser.parse(obj, f);
					if (retparams.get(param.getName()) == null)
					{
						retparams.put(param.getName(), param);
					}
					else
					{
						throw new RuntimeException("the return parameter " + f.getName() + " defined repeated in " + retParamCls.get(i).getName()
								+ ", try in other names");
					}
				}
			}

			ReturnParameterProviderConfig rppc = retParamCls.get(i).getAnnotation(ReturnParameterProviderConfig.class);
			retparamMap.put(rppc.module().newInstance().getName(), retparams);
		}

		ActionParser actionParser = new ActionParser(userInterface, interceptorInvoker);
		// 加入框架内置接口
		actCls.add(0, LoadCssAction.class);
		actCls.add(0, LoadJsAction.class);

		for (int i = 0; i < actCls.size(); i++)
		{
			// 初始化Action
			ActionEntity ae = actionParser.parse(actCls.get(i), paramMap, retparamMap);
			Action act = (Action) ae.getCls().newInstance();
			act.init(ae);
			versions.put(ae.getVersion().getVersion(), ae.getVersion().getVersionIndex());

			List<Action> actlist = this.container.get(ae.getCls().getSimpleName());
			if (actlist == null)
			{
				actlist = new LinkedList<Action>();
				this.container.put(ae.getCls().getSimpleName(), actlist);
			}

			// 如果重复定义
			for (Action a : actlist)
			{
				if (a.getActionEntity().getVersion().getVersionIndex() == ae.getVersion().getVersionIndex())
				{
					throw new RuntimeException("the action " + ae.getCls().getName() + " defined repeated for version="
							+ ae.getVersion().getVersion() + ", try to define it in different version");
				}
			}

			// 加入Action容器，并打印日志
			actlist.add(act);
			logger.debug("Action container load action " + ae.getCls().getName() + " successfully, version is " + ae.getVersion().getVersion());

			// 注入所有@ResourceConfig
			Field[] fs = act.getClass().getDeclaredFields();
			for (Field f : fs)
			{
				if (f.getAnnotation(ResourceConfig.class) != null)
				{
					Object bean = BeanFactory.getBean(f.getType());
					f.setAccessible(true);
					f.set(act, bean);
					logger.debug("inject bean into field " + f.getName() + " in the action " + act.getClass().getName() + ", version is "
							+ ae.getVersion().getVersion());
				}
			}
		}

		// 排序所有接口版本链表
		for (List<Action> actlist : container.values())
		{
			Collections.sort(actlist, new Comparator<Action>()
			{
				@Override
				public int compare(Action o1, Action o2)
				{
					if (o1.getActionEntity().getVersion().getVersionIndex() < o2.getActionEntity().getVersion().getVersionIndex())
					{
						return 1;
					}
					return -1;
				}
			});
		}
		// 日志
		logger.info("ActionContainer initialized successfully");
	}

	/**
	 * 读取action
	 * @param version		接口版本
	 * @param name 			Action名称
	 * @return
	 */
	public Action getAction(String version, String name)
	{
		if (versions.containsKey(version))
		{
			int versionIndex = this.versions.get(version);
			List<Action> actlist = this.container.get(name);
			if (actlist != null && !actlist.isEmpty())
			{
				int length = actlist.size();
				Action item = null;
				for (int i = 0; i < length; i++)
				{
					item = actlist.get(i);
					// 如果命中版本
					if (item.getActionEntity().getVersion().getVersionIndex() <= versionIndex)
					{
						return item;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取所有action
	 * @return
	 */
	public List<Action> getAllActions()
	{
		// List<Action> acts = new ArrayList<Action>(container.size());
		// String key;
		// for (Iterator<String> it = container.keySet().iterator();
		// it.hasNext();)
		// {
		// key = it.next();
		// acts.add(container.get(key));
		// }
		// return acts;
		return null;
	}
}
