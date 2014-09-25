package com.sean.index.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.sean.common.util.AppendObjectOutputStream;
import com.sean.common.util.FileUtil;

public class LocalFileSys implements FileSys
{
	@Override
	public boolean exists(String file)
	{
		return new File(file).exists();
	}

	@Override
	public void mkdir(String dir)
	{
		new File(dir).mkdir();
	}

	@Override
	public void deleteFile(String file)
	{
		new File(file).delete();
	}

	@Override
	public void deleteDir(String dir) throws IOException
	{
		FileUtils.deleteDirectory(new File(dir));
	}

	@Override
	public void cleanDir(String dir) throws IOException
	{
		FileUtils.cleanDirectory(new File(dir));
	}

	@Override
	public FileInputStream openInputStream(String file) throws FileNotFoundException
	{
		return new FileInputStream(file);
	}

	@Override
	public OutputStream openOutputStream(String file) throws FileNotFoundException
	{
		return new FileOutputStream(file);
	}

	@Override
	public void writeObjectToFile(String file, Object obj, boolean append)
	{
		FileUtil.writeObjectToFile(new File(file), obj, append);
	}

	@Override
	public void writeObjectsToFile(String file, List<?> obj, boolean append) throws IOException
	{
		int length = obj.size();
		File f = new File(file);
		int index = 0;
		if (length > 0)
		{
			ObjectOutputStream output = null;
			// 如果是第一次写入
			if (!f.exists() || f.length() == 0)
			{
				output = new ObjectOutputStream(new FileOutputStream(f, true));
				output.writeObject(obj.get(index));
				output.close();
				index++;
			}
			try
			{
				output = new AppendObjectOutputStream(new FileOutputStream(f, true));
				for (int i = index; i < length; i++)
				{
					output.writeObject(obj.get(i));
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

	@Override
	public void writeStringToFile(String file, String data, boolean append) throws IOException
	{
		FileUtils.writeStringToFile(new File(file), data, append);
	}

	@Override
	public Directory openDir(String file) throws IOException
	{
		return FSDirectory.open(new File(file));
	}

	@Override
	public List<String> readLines(String file) throws IOException
	{
		return FileUtils.readLines(new File(file));
	}

	@Override
	public File[] listFiles(String dir) throws IOException
	{
		return new File(dir).listFiles();
	}

	@Override
	public void close() throws IOException
	{
	}
}
