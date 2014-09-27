package com.sean.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * 时间工具类
 * @author Sean
 */
public class TimeUtil
{
	private static final SimpleDateFormat FORMAT_YYYYMMDDHHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat FORMAT_YYYYMMDDHHMMSSLong = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final SimpleDateFormat FORMAT_YYYYMMDDLong = new SimpleDateFormat("yyyyMMdd");

	/**
	 * 按照格式读取时间字符串
	 * @param format				日期格式
	 * @return
	 */
	public static String getDateByFormat(String format)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date());
	}

	/**
	 * 获取yyyy-MM-dd hh:mm:ss格式日期
	 * @return
	 */
	public static String getYYYYMMDDHHMMSSDate()
	{
		return FORMAT_YYYYMMDDHHMMSS.format(new Date());
	}

	/**
	 * 获取yyyMMdd格式日期
	 * @return
	 */
	public static int getYYYYMMDD()
	{
		return Integer.parseInt(FORMAT_YYYYMMDDLong.format(new Date()));
	}

	/**
	 * 获取yyyyMMddhhmmss格式日期
	 * @return
	 */
	public static long getYYYYMMDDHHMMSSTime()
	{
		return Long.parseLong(FORMAT_YYYYMMDDHHMMSSLong.format(new Date()));
	}

	/**
	 * 获取yyyyMMddhhmmss格式日期
	 * @return
	 */
	public static long getYYYYMMDDHHMMSSTime(int year, int month)
	{
		String monthStr = month < 10 ? "0" + month : month + "";
		return Long.parseLong(year + monthStr + "00000000");
	}

	/**
	 * 获取yyyyMMddhhmmss格式日期
	 * @return
	 */
	public static long getYYYYMMDDHHMMSSTime(int year, int month, int day)
	{
		String monthStr = month < 10 ? "0" + month : month + "";
		String dayStr = day < 10 ? "0" + day : day + "";
		return Long.parseLong(year + monthStr + dayStr + "000000");
	}

	/**
	 * 读取指定时间的下N天
	 * @param date					指定时间
	 * @param offset				下N天
	 * @return						
	 */
	public static Date getNextDays(Date date, int offset)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, offset);
		date = calendar.getTime();
		return date;
	}

	/**
	 * 读取一个月的天数
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getDaysOfMonth(int year, int month)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.set(year, month, 1);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 读取唯一的字符串
	 * @return
	 */
	public static synchronized String getUniqueStr()
	{
		return Long.toString(System.currentTimeMillis());
	}

	/**
	 * 字符串转时间
	 * @param str						日期字符串
	 * @param format					日期格式
	 * @return
	 */
	public static Date fromString(String str, String format)
	{
		try
		{
			DateFormat df = new SimpleDateFormat(format);
			Date date = df.parse(str);
			return date;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 读取随机日期
	 * @param beginDate					起始日期
	 * @param endDate					终止日期
	 * @return							起始和终止日期之间的随即日期
	 */
	public static Date randomDate(String beginDate, String endDate)
	{
		try
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date start = format.parse(beginDate);
			Date end = format.parse(endDate);
			int days = (int) ((end.getTime() - start.getTime()) / 86400000);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(start);
			calendar.add(Calendar.DATE, new Random().nextInt(days));
			return calendar.getTime();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
