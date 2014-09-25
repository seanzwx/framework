package com.sean.index.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.lucene.store.Directory;

/**
 * 索引需要操作的文件系统接口
 * 所有的文件路径都是绝对路径
 * @author sean
 */
public interface FileSys
{
	public boolean exists(String file) throws IOException;

	public void mkdir(String dir) throws IOException;

	public void deleteFile(String file) throws IOException;

	public void deleteDir(String dir) throws IOException;

	public void cleanDir(String dir) throws IOException;

	public InputStream openInputStream(String file) throws IOException;

	public OutputStream openOutputStream(String file) throws IOException;

	public void writeObjectToFile(String file, Object obj, boolean append) throws IOException;

	public void writeObjectsToFile(String file, List<?> obj, boolean append) throws IOException;

	public void writeStringToFile(String file, String data, boolean append) throws IOException;

	public Directory openDir(String file) throws IOException;

	public List<String> readLines(String file) throws IOException;

	public File[] listFiles(String dir) throws IOException;

	public void close() throws IOException;
}
