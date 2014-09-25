package com.sean.index.core;

import java.util.Map;

import org.apache.lucene.document.Document;

public interface BackupReader
{
	/**
	 * 从备份中读取文档
	 * @param backupMap				该参数包含了备份时候文档的所有域，但是备份的时候都是保存字符串，所以在这里，用户自行转换相关数据类型
	 * @return						Lucene文档
	 */
	public Document read(Map<String, String> backupMap);
}
