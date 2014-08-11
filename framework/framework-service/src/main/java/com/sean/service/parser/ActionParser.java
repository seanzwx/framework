package com.sean.service.parser;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.sean.persist.core.PersistContext;
import com.sean.persist.dictionary.Dictionary;
import com.sean.persist.entity.DictionaryKeyEntity;
import com.sean.service.annotation.ActionConfig;
import com.sean.service.annotation.DescriptConfig;
import com.sean.service.annotation.MustParamsConfig;
import com.sean.service.annotation.OptionalParamsConfig;
import com.sean.service.annotation.ReturnParamsConfig;
import com.sean.service.core.FrameworkSpi;
import com.sean.service.core.InterceptorInvoker;
import com.sean.service.core.PermissionProvider;
import com.sean.service.core.Version;
import com.sean.service.entity.ActionEntity;
import com.sean.service.entity.ParameterEntity;
import com.sean.service.entity.ReturnParameterEntity;
import com.sean.service.entity.UseDicEntity;
import com.sean.service.enums.Format;
import com.sean.service.worker.ActionWorker;
import com.sean.service.worker.AuthenticateWorker;
import com.sean.service.worker.CrossDomainWorker;
import com.sean.service.worker.InterceptWorker;
import com.sean.service.worker.ParamCheckWorker;
import com.sean.service.worker.PermissionWorker;
import com.sean.service.worker.TransactionWorker;
import com.sean.service.worker.Worker;

/**
 * Action解析器
 * @author sean
 */
public class ActionParser
{
	private FrameworkSpi userInterface;
	private InterceptorInvoker interceptorInvoker;
	private Map<String, Version> versions = new HashMap<String, Version>();

	public ActionParser(FrameworkSpi userInterface, InterceptorInvoker interceptorInvoker)
	{
		this.userInterface = userInterface;
		this.interceptorInvoker = interceptorInvoker;
	}

	public ActionEntity parse(Class<?> cls, Map<String, Map<String, ParameterEntity>> paramMap,
			Map<String, Map<String, ReturnParameterEntity>> retparamMap) throws Exception
	{
		ActionConfig ac = cls.getAnnotation(ActionConfig.class);
		MustParamsConfig mpc = cls.getAnnotation(MustParamsConfig.class);
		OptionalParamsConfig omc = cls.getAnnotation(OptionalParamsConfig.class);
		ReturnParamsConfig rpc = cls.getAnnotation(ReturnParamsConfig.class);
		DescriptConfig dc = cls.getAnnotation(DescriptConfig.class);

		if (dc == null)
		{
			throw new RuntimeException("action必须使用DescriptConfig注解注释");
		}

		Map<String, ParameterEntity> params = paramMap.get(ac.module().newInstance().getName());
		// 解析必填参数
		ParameterEntity[] mustParams = null;
		if (mpc != null)
		{
			String[] mustPc = mpc.value();
			mustParams = new ParameterEntity[mustPc.length];
			for (int i = 0; i < mustPc.length; i++)
			{
				ParameterEntity param = params.get(mustPc[i]);
				if (param == null)
				{
					throw new RuntimeException("the request parameter " + mustPc[i] + " defined in " + cls.getName() + " was not found");
				}
				else
				{
					mustParams[i] = param;
				}
			}
		}
		else
		{
			mustParams = new ParameterEntity[0];
		}

		// 解析可选参数
		ParameterEntity[] optionalParams = null;
		if (omc != null)
		{
			String[] optionalPc = omc.value();
			optionalParams = new ParameterEntity[optionalPc.length];
			for (int i = 0; i < optionalPc.length; i++)
			{
				ParameterEntity param = params.get(optionalPc[i]);
				if (param == null)
				{
					throw new RuntimeException("the request parameter " + optionalPc[i] + " defined in " + cls.getName() + " was not found");
				}
				else
				{
					optionalParams[i] = param;
				}
			}
		}
		else
		{
			optionalParams = new ParameterEntity[0];
		}

		// 解析返回参数
		Map<String, ReturnParameterEntity> retparams = retparamMap.get(ac.module().newInstance().getName());
		ReturnParameterEntity[] rpe = null;
		if (rpc != null)
		{
			String[] rpcs = rpc.value();
			rpe = new ReturnParameterEntity[rpcs.length];
			for (int i = 0; i < rpcs.length; i++)
			{
				ReturnParameterEntity param = retparams.get(rpcs[i]);
				if (param == null)
				{
					throw new RuntimeException("the return parameter " + rpcs[i] + " defined in " + cls.getName() + " was not found");
				}
				else
				{
					rpe[i] = param;
				}
			}
		}
		else
		{
			rpe = new ReturnParameterEntity[0];
		}

		// 开始创建工作流,完整流程为：crossdomain(must)->authenticate(optional)->intercept(must)->checkparam(must)->permission(optional)->transaction(optional)->action(must)
		Worker actionWorker = new ActionWorker(this.userInterface);
		Worker chain = actionWorker;
		// 如果需要事务
		if (ac.transaction())
		{
			Worker node = new TransactionWorker(chain);
			chain = node;
		}
		// 如果需要权限验证
		if (ac.permission() != PermissionProvider.None)
		{
			Worker node = new PermissionWorker(userInterface, chain);
			chain = node;
		}
		// 添加参数验证
		if (true)
		{
			Worker node = new ParamCheckWorker(chain);
			chain = node;
		}
		// 添加拦截器
		if (true)
		{
			Worker node = new InterceptWorker(interceptorInvoker, chain);
			chain = node;
		}
		// 如果需要认证
		if (ac.authenticate())
		{
			Worker node = new AuthenticateWorker(userInterface, chain);
			chain = node;
		}
		// 添加跨域
		if (true)
		{
			Worker node = new CrossDomainWorker(chain);
			chain = node;
		}

		Version version = this.versions.get(ac.version().getName());
		if (version == null)
		{
			version = (Version) ac.version().newInstance();
			versions.put(ac.version().getName(), version);
		}

		ActionEntity ae = new ActionEntity(ac.transaction(), ac.module(), rpe, mustParams, optionalParams, ac.permission(), ac.authenticate(),
				ac.returnType(), cls, dc.value(), version, chain);

		// 检查action
		checkAction(ae);

		return ae;
	}

	/**
	 * 检查action
	 * @param ae
	 */
	private void checkAction(ActionEntity ae) throws Exception
	{
		ReturnParameterEntity[] params = ae.getReturnParams();
		for (int i = 0; i < params.length; i++)
		{
			ReturnParameterEntity param = params[i];
			// 如果返回参数是实体
			if (param.getFormat() == Format.Entity || param.getFormat() == Format.EntityList)
			{
				checkEntity(ae, param);
			}
		}
	}

	/**
	 * 检查实体
	 * @param ae
	 * @param param
	 */
	private void checkEntity(ActionEntity ae, ReturnParameterEntity param) throws Exception
	{
		Map<String, Class<?>> allFields = new HashMap<String, Class<?>>();

		// 先获取实体所有域
		Field[] fields = param.getEntity().getDeclaredFields();
		for (int i = 0; i < fields.length; i++)
		{
			allFields.put(fields[i].getName(), fields[i].getType());
		}

		// 获取数据字典所有域
		UseDicEntity[] dics = param.getDics();
		for (int i = 0; i < dics.length; i++)
		{
			Dictionary dic = PersistContext.CTX.getDictionary(dics[i].getDic());
			if (dic == null)
			{
				throw new RuntimeException("the dictionary " + dics[i].getDic() + " was not found");
			}
			DictionaryKeyEntity[] keys = dic.getEntity().getKeys();
			for (int j = 0; j < keys.length; j++)
			{
				allFields.put(keys[j].getKey(), String.class);
			}
		}

		// 判断返回参数
		String[] paramFields = param.getFields();
		for (int i = 0; i < paramFields.length; i++)
		{
			if (allFields.get(paramFields[i]) == null)
			{
				throw new RuntimeException("the return parameter field " + paramFields[i] + " of action " + ae.getCls().getName()
						+ " was not found both in entity and dictionary");
			}
		}
	}
}
