package com.sean.service.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.sean.service.action.InquireApiAction;
import com.sean.service.action.InquireApiListAction;
import com.sean.service.enums.ReturnType;

/**
 * Action伪实现调度控制器
 * @author sean
 */
public final class ActionPseudoDispatcher implements Filter
{
	private final RequestChecker checker = new RequestChecker();
	private HttpWriter httpWriter = new HttpWriterServletImpl();

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		Action act = null;
		Session session = null;
		String json = null;
		boolean execute = false;
		try
		{
			// 转换编码
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");

			// 分析url，读取ActionName
			String[] tmp = ((HttpServletRequest) request).getRequestURI().split("/");
			String actName = tmp[tmp.length - 1];
			String version = tmp[tmp.length - 2];

			// 获取Action
			act = FrameworkContext.CTX.getAction(version, actName);
			if (act == null)
			{
				json = "{\"state\":\"ActionNotExists\",\"data\":{}}";
				return;
			}

			// 创建会话
			session = new SessionServletImpl(act, request, response);

			// 验证参数合法性
			if (!act.checkParams(session, checker))
			{
				json = "{\"state\":\"Invalid\",\"msg\":\"" + session.getMsg() + "\"}";
				return;
			}

			// 执行伪实现
			ReturnType rt = act.getActionEntity().getReturnType();
			Class<?> clazz = act.getActionEntity().getCls();
			if (clazz == InquireApiAction.class || clazz == InquireApiListAction.class)
			{
				execute = true;
				act.execute(session);
			}
			else if (rt == ReturnType.Js || rt == ReturnType.Css)
			{
				execute = true;
				act.execute(session);
			}
			else
			{
				json = "{\"state\":\"Success\",\"data\":" + act.pseudo(session) + "}";
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			// 客户端异常标记
			session.exception();
		}
		finally
		{
			try
			{
				if (!execute)
				{
					this.httpWriter.writeJson(request, response, json);
				}
				else
				{
					httpWriter.write(request, response, session, act.getActionEntity());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException
	{
	}

	@Override
	public void destroy()
	{
	}
}
