package com.sean.config.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.sean.config.enums.ConfigEnum;
import com.sean.config.enums.L;
import com.sean.log.core.LogFactory;

/**
 * 架构配置
 * @author sean
 */
public class Config
{
	private static Map<String, String> config = new HashMap<>();
	private static final Logger logger = LogFactory.getLogger(L.Config);

	/**
	 * 读取配置属性
	 * @param name
	 * @return
	 */
	public static String getProperty(ConfigEnum name)
	{
		return config.get(name.toString());
	}

	/**
	 * 不建议使用读取配置属性
	 * @param name
	 * @return
	 */
	public static String getProperty(String name)
	{
		return config.get(name);
	}

	/**
	 * 读取服务配置文件信息
	 * @param targetUser
	 * @return
	 */
	public synchronized static void readConfiguration()
	{
		logger.info("read config.properties...");
		try
		{
			// 读取基本配置
			InputStream is = Config.class.getResourceAsStream("/config.properties");
			Properties props = new Properties();
			props.load(is);
			is.close();
			for (Object key : props.keySet())
			{
				config.put(key.toString(), props.getProperty(key.toString()));
			}

			// 默认开发配置
			String targetFile = "/config_dev.properties";
			String targetUser = config.get("type");
			if (targetUser != null && targetUser.equals("qa"))
			{
				targetFile = "/config_qa.properties";
			}
			if (targetUser != null && targetUser.equals("server"))
			{
				targetFile = "/config_server.properties";
			}

			// 读取目标用户配置
			try
			{
				is = Config.class.getResourceAsStream(targetFile);
				props = new Properties();
				props.load(is);
				is.close();
				for (Object key : props.keySet())
				{
					config.put(key.toString(), props.getProperty(key.toString()));
				}
			}
			catch (Exception e)
			{
				logger.info("target user configuration file not found.");
			}

			// 打印日志
			StringBuilder sb = new StringBuilder("");
			sb.append("read config from config.properties\n");
			sb.append("***********************************************************\n");
			sb.append("*             the configuration of context                 \n");
			sb.append("*----------------------------------------------------------");
			for (String key : config.keySet())
			{
				sb.append("\n* ").append(key).append("=").append(config.get(key));
			}
			sb.append("\n***********************************************************");
			logger.info(sb.toString());

			logger.info("Read config.properties finished.");
		}
		catch (Exception e)
		{
			logger.error("读取配置文件错误:" + e.getMessage(), e);
		}
	}
}
