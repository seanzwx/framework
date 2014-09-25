package com.sean.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件工具类
 * 
 * @author Sean
 * 
 */
public class FileUtil
{
	/**
	 * 追加内容到文件的倒数第line行
	 * @param filename
	 * @param content
	 * @param line							倒数第几行
	 */
	public static void appendToFile(String filename, String content, int line)
	{
		try
		{
			RandomAccessFile randomFile = new RandomAccessFile(filename, "rw");
			long fileLength = randomFile.length();
			randomFile.seek(fileLength);

			long end = randomFile.getFilePointer();
			int j = 0;
			long a = 0;
			while ((end >= 0) && (j <= 2))
			{
				end--;
				randomFile.seek(end);
				byte n = randomFile.readByte();
				if (n == '\n')
				{
					a = randomFile.getFilePointer();
					j++;
					if (j == line)
					{
						break;
					}
				}
			}
			randomFile.seek(a);
			randomFile.writeBytes(content);
			randomFile.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 写文件
	 * @param file					文件
	 * @param path					目标文件名
	 */
	public static void writeFileToDisk(File file, String targetPath)
	{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try
		{
			fis = new FileInputStream(file);
			fos = new FileOutputStream(targetPath);
			byte[] b = new byte[1];
			while (fis.read(b) != -1)
			{
				fos.write(b);
			}
			fos.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (fis != null)
				{
					fis.close();
					fis = null;
				}
				if (fos != null)
				{
					fos.close();
					fos = null;
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 读取字符串文件
	 * @param filepath
	 * @return
	 */
	public static String readFileFromDisk(String filepath)
	{
		FileInputStream fis = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		String temp = "";
		try
		{
			fis = new FileInputStream(filepath);
			br = new BufferedReader(new InputStreamReader(fis, "utf-8"));
			while ((temp = br.readLine()) != null)
			{
				sb.append(temp).append("\n");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (fis != null)
				{
					fis.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 删除文件
	 * @param filepath
	 */
	public static void deleteFile(String filepath)
	{
		new File(filepath).delete();
	}

	/**
	 * url转成html文件
	 * @param strUrl					url
	 * @param filename					保存的html文件绝对路径
	 */
	public static void urlToHtml(String strUrl, String filename)
	{
		URL url = null;
		HttpURLConnection connection = null;
		FileOutputStream fos = null;
		try
		{
			url = new URL(strUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.connect();

			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
			// out.writeBytes(content);
			out.flush();
			out.close();

			fos = new FileOutputStream(filename);

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
			StringBuffer buffer = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null)
			{
				buffer.append(line);
			}
			reader.close();
			fos.write(buffer.toString().getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (connection != null)
				{
					connection.disconnect();
				}
				if (fos != null)
				{
					fos.close();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void writeObjectToFile(File file, Object obj, boolean append)
	{
		ObjectOutputStream output = null;
		try
		{
			if (!file.exists() || file.length() == 0)
			{
				output = new ObjectOutputStream(new FileOutputStream(file, true));
			}
			else
			{
				output = new AppendObjectOutputStream(new FileOutputStream(file, true));
			}
			output.writeObject(obj);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (output != null)
				{
					output.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
