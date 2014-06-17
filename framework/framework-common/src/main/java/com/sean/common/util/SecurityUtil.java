package com.sean.common.util;


/**
 * 加密类
 * @author Sean
 */
public final class SecurityUtil
{
	/**
	 * des加密
	 * @param text
	 * @param encryptKey
	 * @return				失败返回null
	 */
	public static String desEncrypt(String text, String encryptKey)
	{
		try
		{
			DESPlus desplus = new DESPlus(encryptKey);
			return desplus.encrypt(text);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * des解密
	 * @param text
	 * @param encryptKey
	 * @return				 失败返回null
	 */
	public static String desDecrypt(String text, String encryptKey)
	{
		try
		{
			DESPlus desplus = new DESPlus(encryptKey);
			return desplus.decrypt(text);
		}
		catch (Exception e)
		{
			return null;
		}
	}
}