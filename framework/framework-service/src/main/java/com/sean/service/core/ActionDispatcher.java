package com.sean.service.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.service.enums.L;

/**
 * Action调度控制器
 * @author sean
 */
public final class ActionDispatcher implements Filter
{
	private HttpWriter httpWriter;
	private FrameworkSpi userInterface;
	private final Logger logger = LogFactory.getLogger(L.Service);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		Action act = null;
		Session session = null;
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		try
		{
			// 分析url，读取ActionName和Version
			String[] tmp = ((HttpServletRequest) request).getRequestURI().split("/");
			String actName = tmp[tmp.length - 1];
			String actVersion = tmp[tmp.length - 2];

			act = FrameworkContext.CTX.getAction(actVersion, actName);
			if (act == null)
			{
				httpWriter.writeJson(request, response, "{\"state\":\"ActionNotExists\",\"data\":{\"action\":\"" + actName + "\"}}");
				return;
			}

			session = new SessionServletImpl(act, request, response);
			act.getActionEntity().getWorker().work(session, act);
		}
		catch (BusinessException be)
		{
			session.businessException(be.getMessage(), be.getCode());
		}
		catch (Exception e)
		{
			// 清空所有返回参数,客户端异常标记
			session.clearReturnAttribute();
			session.exception();
			// 全局异常处理
			userInterface.exceptionHandle(session, act.getActionEntity(), e);
		}
		finally
		{
			try
			{
				// 写到客户端
				if (session != null)
				{
					httpWriter.write(request, response, session, act.getActionEntity());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
			// 销毁用户上下文
			userInterface.destoryUserContext();
		}
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException
	{
		// 获取用户接口
		this.userInterface = FrameworkContext.CTX.getUserInterface();
		httpWriter = new HttpWriterServletImpl();
	}

	@Override
	public void destroy()
	{
	}
}
