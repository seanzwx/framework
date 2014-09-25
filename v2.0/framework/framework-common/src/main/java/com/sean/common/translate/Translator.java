package com.sean.common.translate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 翻译网络机器人
 * @author sean
 *
 */
public class Translator
{
	private static String URL = "http://translate.google.cn/translate_a/t?client=t&text={text}&hl=zh-CN&sl={source}&tl={target}&ie=UTF-8&oe=UTF-8&multires=1&prev=conf&psl=zh-CN&ptl=zh-CN&otf=1&it=sel.6041&ssel=0&tsel=0&sc=1";
	
	/**
	 * 翻译接口
	 * @param text					翻译文本
	 * @param source				源语言
	 * @param target				目标语言
	 * @return
	 */
	public static String translate(String text, Language source, Language target)
	{
		if (source == target)
		{
			return text;
		}
		try
		{
			// 处理参数
			String urlTxt = URL.replace("{text}", URLEncoder.encode(text, "utf-8"));
			urlTxt = urlTxt.replace("{source}", source.toString());
			urlTxt = urlTxt.replace("{target}", target.toString());
			return doTranslate(text, urlTxt);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return text;
		}
	}
	
	/**
	 * 翻译接口
	 * @param text					翻译文本
	 * @param source				源语言标识符
	 * @param target				目标语言标识符
	 * @return
	 */
	public static String translate(String text, String source, String target)
	{
		if (source.equals(target))
		{
			return text;
		}
		try
		{
			// 处理参数
			String urlTxt = URL.replace("{text}", URLEncoder.encode(text, "utf-8"));
			urlTxt = urlTxt.replace("{source}", source);
			urlTxt = urlTxt.replace("{target}", target);
			return doTranslate(text, urlTxt);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return text;
		}
	}

	/**
	 * 翻译接口（自动检测语言）
	 * @param text					翻译文本			
	 * @param target				目标语言标识符
	 * @return
	 */
	public static String translate(String text, String target)
	{
		try
		{
			// 处理参数
			String urlTxt = URL.replace("{text}", URLEncoder.encode(text, "utf-8"));
			urlTxt = urlTxt.replace("{source}", "auto");
			urlTxt = urlTxt.replace("{target}", target);
			return doTranslate(text, urlTxt);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return text;
		}
	}
	
	/**
	 * 翻译接口
	 * @param text					翻译文本
	 * @param target				目标语言
	 * @return
	 */
	public static String translate(String text, Language target)
	{
		try
		{
			// 处理参数
			String urlTxt = URL.replace("{text}", URLEncoder.encode(text, "utf-8"));
			urlTxt = urlTxt.replace("{source}", "auto");
			urlTxt = urlTxt.replace("{target}", target.toString());
			return doTranslate(text, urlTxt);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return text;
		}
	}
	
	/**
	 * 翻译接口（自动检测语言）
	 * @param text					翻译文本			
	 * @param target				目标语言标识符
	 * @return
	 */
	public static String translate(String url, String text, String target, int type)
	{
		try
		{
			// 处理参数
			String urlTxt = url.replace("{text}", URLEncoder.encode(text, "utf-8"));
			urlTxt = urlTxt.replace("{source}", "auto");
			urlTxt = urlTxt.replace("{target}", target);
			return doTranslate(text, urlTxt);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return text;
		}
	}

	private static String doTranslate(String text, String urlTxt)
	{
		HttpURLConnection urlConnection = null;
		try
		{	
			URL url = new URL(urlTxt);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setConnectTimeout(5000);
			urlConnection.setDoInput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");

			InputStream in = urlConnection.getInputStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			StringBuffer temp = new StringBuffer(1024);
			String line = bufferedReader.readLine();
			while (line != null)
			{
				temp.append(line).append("/r/n");
				line = bufferedReader.readLine();
			}
			bufferedReader.close();
			String newStr = temp.toString();
			if (newStr.startsWith("TranslateApiException"))
			{
				return text;
			}
			return parse(temp.toString());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return text;
		}
		finally
		{
			if (urlConnection != null)
				urlConnection.disconnect();
		}
	}
	
	/**
	 * 解析Google返回的翻译字符串
	 * @param text
	 * @return
	 */
	private static String parse(String text)
	{
		try
		{
			String tmp = text.substring(3);
			tmp = tmp.split("\",\"")[0];
			tmp = tmp.substring(1, tmp.length());
			return tmp;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return text;
		}
	}
}
