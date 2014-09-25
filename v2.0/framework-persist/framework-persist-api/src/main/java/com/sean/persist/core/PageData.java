package com.sean.persist.core;

import java.util.List;

/**
 * 分页数据
 * 
 * @author Sean
 * 
 * @param <E>
 */
public class PageData<E extends Entity>
{
	private List<E> datas;
	private int totalrecords;
	private int pageNo;

	public List<E> getDatas()
	{
		return datas;
	}

	public void setDatas(List<E> datas)
	{
		this.datas = datas;
	}

	public int getTotalrecords()
	{
		return totalrecords;
	}

	public void setTotalrecords(int totalrecords)
	{
		this.totalrecords = totalrecords;
	}

	public int getPageNo()
	{
		return pageNo;
	}

	public void setPageNo(int pageNo)
	{
		this.pageNo = pageNo;
	}

}
