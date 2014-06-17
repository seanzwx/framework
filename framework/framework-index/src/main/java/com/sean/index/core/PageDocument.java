package com.sean.index.core;

import java.util.List;

import org.apache.lucene.document.Document;

import com.sean.index.util.IndexUtil;

public final class PageDocument
{
	private int pageNo;
	private int totalrecords;
	private List<Document> data;

	public PageDocument(int pageNo, int totalrecords, List<Document> data)
	{
		this.pageNo = pageNo;
		this.totalrecords = totalrecords;
		this.data = data;
	}

	public int getPageNo()
	{
		return pageNo;
	}

	public int getTotalrecords()
	{
		return totalrecords;
	}

	public List<Document> getData()
	{
		return data;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("pageNo=" + pageNo).append("\n");
		sb.append("totalrecords=" + totalrecords).append("\n");
		int length = data.size();
		for (int i = 0; i < length; i++)
		{
			sb.append(IndexUtil.documentToString(data.get(i))).append("\n");
		}
		return sb.toString();
	}

}
