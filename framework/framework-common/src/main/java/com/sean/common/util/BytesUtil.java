package com.sean.common.util;

import java.io.UnsupportedEncodingException;

/**
 * 字节工具
 * @author sean
 */
public class BytesUtil
{
	public static byte[] toBytes(long number)
	{
		long temp = number;
		byte[] b = new byte[8];
		for (int i = 0; i < b.length; i++)
		{
			b[i] = new Long(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static void toBytes(byte[] b, int index, long number)
	{
		long temp = number;
		for (int i = 0; i < 8; i++)
		{
			b[index + i] = new Long(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
	}

	public static long toLong(byte[] b)
	{
		return toLong(b, 0);
	}

	public static long toLong(byte[] b, int index)
	{
		long s = 0;
		long s0 = b[index + 0] & 0xff;
		long s1 = b[index + 1] & 0xff;
		long s2 = b[index + 2] & 0xff;
		long s3 = b[index + 3] & 0xff;
		long s4 = b[index + 4] & 0xff;
		long s5 = b[index + 5] & 0xff;
		long s6 = b[index + 6] & 0xff;
		long s7 = b[index + 7] & 0xff;
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}

	public static byte[] toBytes(int number)
	{
		int temp = number;
		byte[] b = new byte[4];
		for (int i = 0; i < b.length; i++)
		{
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static void toBytes(byte[] b, int index, int number)
	{
		int temp = number;
		for (int i = 0; i < 4; i++)
		{
			b[index + i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
	}

	public static int toInt(byte[] b)
	{
		return toInt(b, 0);
	}

	public static int toInt(byte[] b, int index)
	{
		int s = 0;
		int s0 = b[index + 0] & 0xff;
		int s1 = b[index + 1] & 0xff;
		int s2 = b[index + 2] & 0xff;
		int s3 = b[index + 3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return s;
	}

	public static byte[] toBytes(short number)
	{
		int temp = number;
		byte[] b = new byte[2];
		for (int i = 0; i < b.length; i++)
		{
			b[i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static byte[] toBytes(byte[] b, int index, short number)
	{
		int temp = number;
		for (int i = 0; i < 2; i++)
		{
			b[index + i] = new Integer(temp & 0xff).byteValue();
			temp = temp >> 8;
		}
		return b;
	}

	public static short toShort(byte[] b)
	{
		return toShort(b, 0);
	}

	public static short toShort(byte[] b, int index)
	{
		short s = 0;
		short s0 = (short) (b[index + 0] & 0xff);
		short s1 = (short) (b[index + 1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}

	public static byte[] toBytes(float f)
	{
		int fbit = Float.floatToIntBits(f);
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++)
		{
			b[i] = (byte) (fbit >> (24 - i * 8));
		}
		int len = b.length;
		byte[] dest = new byte[len];
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		for (int i = 0; i < len / 2; ++i)
		{
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}
		return dest;
	}

	public static float toFloat(byte[] b)
	{
		int l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		return Float.intBitsToFloat(l);
	}

	public static byte[] toBytes(String str)
	{
		return str.getBytes();
	}

	public static void toBytes(byte[] b, int index, String str)
	{
		try
		{
			byte[] tmp = str.getBytes("utf-8");
			for (int i = 0; i < tmp.length; i++)
			{
				b[index + i] = tmp[i];
			}
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	public static String toString(byte[] b)
	{
		try
		{
			return new String(b, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String toString(byte[] b, int index, int length)
	{
		try
		{
			byte[] tmp = new byte[length];
			for (int i = 0; i < length; i++)
			{
				tmp[i] = b[index + i];
			}
			return new String(tmp, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String toString(byte[] b, int index)
	{
		try
		{
			return new String(b, index, b.length - index, "utf-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将src赋值如b
	 * @param b
	 * @param index
	 * @param src
	 */
	public static void toBytes(byte[] b, int index, byte[] src)
	{
		toBytes(b, index, src, src.length);
	}

	/**
	 * 将src赋值如b
	 * @param b
	 * @param index
	 * @param src
	 * @param len
	 */
	public static void toBytes(byte[] b, int index, byte[] src, int len)
	{
		for (int i = 0; i < len; i++)
		{
			b[i + index] = src[i];
		}
	}

	/**
	 * 截取字节数组
	 * @param b
	 * @param index
	 * @param len
	 * @return
	 */
	public static byte[] subBytes(byte[] b, int index, int len)
	{
		byte[] dest = new byte[len];
		for (int i = 0; i < len; i++)
		{
			dest[i] = b[index + i];
		}
		return dest;
	}

	/**
	 * 截取字节数组
	 * @param b
	 * @param index
	 * @param len
	 * @return
	 */
	public static byte[] subBytes(byte[] b, int index)
	{
		int len = b.length - index;
		byte[] dest = new byte[len];
		for (int i = 0; i < len; i++)
		{
			dest[i] = b[index + i];
		}
		return dest;
	}
}
