package com.sean.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 类扫描器
 * @author Sean
 */
public final class ClassScanner
{
	/**
	 * 扫描包下的所有类
	 * @param classloader					系统类加载器
	 * @param packageNames					包名数组
	 * @return
	 */
	public List<String> findClass(ClassLoader classloader, String[] packageNames) throws Exception
	{
		List<String> classNameList = new ArrayList<String>(1024);
		Enumeration<URL> eUrl = null;
		try
		{
			for (String packageName : packageNames)
			{
				// 获取有效的url
				eUrl = classloader.getResources(this.getPackagePath(packageName));
				if (eUrl != null)
				{
					while (eUrl.hasMoreElements())
					{
						// 获取class路径
						this.findClass(classNameList, classloader, eUrl.nextElement(), packageName);
					}
				}
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		return classNameList;
	}

	/**
	 * 包名与路径转换
	 */
	private String getPackagePath(String packageName)
	{
		return packageName == null ? null : packageName.replace('.', '/');
	}

	/**
	 * 类路径与类名转换
	 */
	private String getPackageName(String packagePath)
	{
		return packagePath == null ? null : packagePath.replace('/', '.');
	}

	/**
	 * 根据url获取包含的class文件
	 */
	private void findClass(List<String> classNameList, ClassLoader classloader, URL url, String packageName) throws Exception
	{
		// 判断是否是jar包
		String urlName = url.getFile();
		if (urlName.indexOf("/src/test/") == -1 && urlName.indexOf("/src/main/") == -1)
		{
			int index = urlName.lastIndexOf(".jar");
			if (index > -1)
			{
				String jarUrlName = urlName.substring(0, index + 4);
				if (!jarUrlName.startsWith("file:"))
				{
					jarUrlName = "file:" + jarUrlName;
				}
				URL jarUrl = new URL(jarUrlName);
				this.findClassInJar(classNameList, classloader, jarUrl);
			}
			else
			{
				this.findClassInDirectory(classNameList, classloader, new File(url.getPath()), packageName);
			}
		}
	}

	/**
	 * 获取文件目录中的class
	 */
	private void findClassInDirectory(List<String> classNameList, ClassLoader classloader, File dir, String packageName) throws Exception
	{
		File tmp = new File(dir.getAbsolutePath().replace("%20", " "));
		File[] child = tmp.listFiles();
		for (int i = 0; i < child.length; i++)
		{
			File file = child[i];
			if (file.isDirectory())
			{
				findClassInDirectory(classNameList, classloader, file, packageName + "." + file.getName());
			}
			else
			{
				if (file.getName().endsWith(".class"))
				{
					this.addClass(classNameList, packageName, file);
				}
			}
		}
	}

	/**
	 * 加入目标class
	 */
	private void addClass(List<String> classNameList, String packageName, File classFile)
	{
		String className = packageName + "." + classFile.getName();
		className = className.substring(0, className.length() - 6);
		if (!classNameList.contains(className))
		{
			classNameList.add(className);
		}
	}

	/**
	 * 加入目标class
	 * @param classNameList
	 * @param className
	 */
	private void putClassName(List<String> classNameList, String className)
	{
		if (!classNameList.contains(className))
		{
			classNameList.add(className);
		}
	}

	/**
	 * 获取jar包的class
	 * 
	 * @param jarUrlName
	 * @param resourceList
	 */
	private void findClassInJar(List<String> classNameList, ClassLoader classloader, URL url) throws Exception
	{
		InputStream is = null;
		try
		{
			is = url.openStream();
			JarInputStream jarInput = new JarInputStream(is);
			String entryName;
			String className;
			String classPath;
			for (JarEntry entry; (entry = jarInput.getNextJarEntry()) != null;)
			{
				if (!entry.isDirectory())
				{
					entryName = entry.getName();
					if (entryName.endsWith(".class"))
					{
						classPath = entryName.substring(0, entryName.length() - 6);
						className = this.getPackageName(classPath);
						this.putClassName(classNameList, className);
					}
				}
			}
			jarInput.close();
		}
		finally
		{
			if (is != null)
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					throw e;
				}
			}
		}
	}
}
