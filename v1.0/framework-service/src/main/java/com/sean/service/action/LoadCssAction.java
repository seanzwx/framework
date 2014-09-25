package com.sean.service.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.sean.config.core.Config;
import com.sean.config.enums.ConfigEnum;
import com.sean.service.annotation.ActionConfig;
import com.sean.service.annotation.DescriptConfig;
import com.sean.service.annotation.MustParamsConfig;
import com.sean.service.core.Action;
import com.sean.service.core.ApplicationContext;
import com.sean.service.core.Session;
import com.sean.service.enums.ReturnType;

@ActionConfig(authenticate = false, module = _M.class, returnType = ReturnType.Css)
@MustParamsConfig(_P.css)
@DescriptConfig("加载合并css脚本")
public final class LoadCssAction extends Action
{
	private Map<String, String> cache = new HashMap<String, String>();
	private SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
	private static final boolean isDev;
	static
	{
		if ("dev".equals(Config.getProperty(ConfigEnum.ServiceMode)))
		{
			isDev = true;
		}
		else
		{
			isDev = false;
		}
	}

	@Override
	public void execute(Session session) throws Exception
	{
		String css = deleteBlank(session.getParameter("css"));

		// 开发模式开启实时脚本
		if (isDev)
		{
			String cssstr = this.readRTStyles(session.getRootPath(), css);
			session.setReturnCss(cssstr);
			return;
		}

		String ifModifiedSince = session.getHeader("If-Modified-Since");
		// 浏览器没有缓存
		if (ifModifiedSince == null)
		{
			String cssstr = this.readStyles(session.getRootPath(), css);
			session.setReturnCss(cssstr);
		}
		// 浏览器有缓存，验证是否过期
		else
		{
			long time = 0;
			try
			{
				time = sdf.parse(ifModifiedSince).getTime();
			}
			catch (Exception e)
			{
				time = 0;
			}
			long launchTime = ApplicationContext.CTX.getLaunchTime();
			if (time <= launchTime)
			{
				String cssstr = this.readStyles(session.getRootPath(), css);
				session.setReturnCss(cssstr);
			}
		}
	}

	/**
	 * 读取css脚本
	 */
	private String readStyles(String rootPath, String css) throws Exception
	{
		// 读取js代码
		int totalLength = 0;
		String[] tmp = css.split(",");
		String code = null;
		File file = null;
		List<String> files = new ArrayList<String>(tmp.length);
		for (int i = 0; i < tmp.length; i++)
		{
			code = cache.get(tmp[i]);
			if (code == null)
			{
				file = new File(rootPath + tmp[i]);
				if (file.exists())
				{
					code = "\r\n/* " + tmp[i] + " */\r\n" + FileUtils.readFileToString(file);
					files.add(code);
					totalLength += code.length();

					cache.put(tmp[i], code);
				}
			}
			else
			{
				files.add(code);
			}
		}

		// 合并css代码
		StringBuilder sb = new StringBuilder(totalLength);
		int length = files.size();
		for (int i = 0; i < length; i++)
		{
			sb.append(files.get(i));
		}

		return sb.toString();
	}

	/**
	 * 开发模式和伪实现模式实时读取脚本
	 */
	private String readRTStyles(String rootPath, String css) throws Exception
	{
		StringBuilder sb = new StringBuilder(102400);
		String[] tmp = css.split(",");
		for (int i = 0; i < tmp.length; i++)
		{
			File file = new File(rootPath + tmp[i]);
			if (file.exists())
			{
				sb.append("\r\n/* ").append(tmp[i]).append(" */\r\n");
				sb.append(FileUtils.readFileToString(file));
			}
		}
		return sb.toString();
	}

	public String deleteBlank(String js)
	{
		StringBuilder sb = new StringBuilder(js.length());
		int length = js.length();
		char c;
		for (int i = 0; i < length; i++)
		{
			c = js.charAt(i);
			if (c != ' ')
			{
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
