package com.sean.service.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sean.log.core.LogFactory;
import com.sean.service.entity.ActionEntity;
import com.sean.service.enums.L;
import com.sean.service.enums.ReturnType;

/**
 * httpwriter 标准servlet实现
 * @author sean
 */
public final class HttpWriterServletImpl implements HttpWriter
{
	private static final Logger logger = LogFactory.getLogger(L.Service);

	@Override
	public void write(ServletRequest request, ServletResponse response, Session session, ActionEntity action) throws Exception
	{
		if (action.getReturnType() == ReturnType.Json)
		{
			writeJson(response, session, action);
		}
		else if (action.getReturnType() == ReturnType.Js)
		{
			writeJs(response, session);
		}
		else if (action.getReturnType() == ReturnType.Css)
		{
			writeCss(response, session);
		}
		else if (action.getReturnType() == ReturnType.File)
		{
			writeFile(response, session);
		}
	}

	@Override
	public void writeJson(ServletRequest request, ServletResponse response, String json) throws Exception
	{
		PrintWriter pw = response.getWriter();
		// 如果是jsonp请求
		String callback = request.getParameter("callback");
		if (callback != null)
		{
			json = callback + "('" + json + "')";
		}
		pw.write(json);
		pw.flush();
	}

	/**
	 * 写入客户端json
	 */
	private void writeJson(ServletResponse response, Session session, ActionEntity action) throws Exception
	{
		response.setContentType("application/json;charset=utf-8");
		PrintWriter pw = response.getWriter();
		try
		{
			String json = JsonWriter.toJson(session, action);

			// 如果客户端使用jsonp
			String callback = session.getParameter("callback");
			if (callback != null && !callback.isEmpty())
			{
				json = callback + "('" + json + "')";
			}
			pw.write(json);
			pw.flush();

			logger.debug("write json to client : " + json);
		}
		finally
		{
			pw.close();
		}
	}

	/**
	 * 写入客户端js
	 */
	private void writeJs(ServletResponse rs, Session session) throws Exception
	{
		HttpServletResponse response = (HttpServletResponse) rs;

		PrintWriter pw = response.getWriter();
		try
		{
			Object js = session.getReturnAttributeMap().get("js");
			// 浏览器没有加载过
			if (js != null)
			{
				response.setContentType("application/x-javascript;charset=utf-8");
				response.addHeader("Cache-Control", "max-age=31536000");
				// response.addHeader("Cache-Control", "no-cache");
				response.addDateHeader("Last-Modified", System.currentTimeMillis());

				pw.write(js.toString());
				pw.flush();
			}
			// 浏览器加载过了
			else
			{
				response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			}
		}
		finally
		{
			pw.close();
		}
	}

	/**
	 * 写入客户端css
	 */
	private void writeCss(ServletResponse rs, Session session) throws Exception
	{
		HttpServletResponse response = (HttpServletResponse) rs;

		PrintWriter pw = response.getWriter();
		try
		{
			Object css = session.getReturnAttributeMap().get("css");
			if (css != null)
			{
				response.setContentType("text/css;charset=utf-8");
				response.addHeader("Cache-Control", "max-age=31536000");
				// response.addHeader("Cache-Control", "no-cache");
				response.addDateHeader("Last-Modified", System.currentTimeMillis());

				pw.write(css.toString());
				pw.flush();
			}
			else
			{
				response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			}
		}
		finally
		{
			pw.close();
		}
	}

	/**
	* 写入客户端File
	*/
	private void writeFile(ServletResponse response, Session session) throws Exception
	{
		Object obj = session.getReturnAttributeMap().get("file");
		if (obj != null)
		{
			File file = (File) obj;
			if (file.exists())
			{
				response.setContentLength((int) file.length());

				ServletOutputStream out = response.getOutputStream();
				InputStream inStream = new FileInputStream(file);
				try
				{
					HttpServletResponse resp = (HttpServletResponse) response;
					resp.setHeader("Content-Disposition", "attachment;filename=" + file.getName());

					
					int readLength;
					byte[] buf = new byte[10240];
					while (((readLength = inStream.read(buf)) != -1))
					{
						out.write(buf, 0, readLength);
					}
					out.flush();
				}
				finally
				{
					inStream.close();
					out.close();
				}
			}
		}
	}
}
