package com.sean.persist.util;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.sean.persist.ext.Condition;
import com.sean.persist.ext.Order;
import com.sean.persist.ext.Value;

/**
 * EntityDao工具类
 * @author sean
 */
public final class EntityDaoUtil
{	
	/**
	 * 获取条件字符串
	 * @param conds
	 * @return
	 */
	public static String getConditionStr(List<Condition> conds)
	{
		int length = conds.size();
		StringBuilder sb = new StringBuilder(20 * length);
		Condition cond = null;
		for (int i = 0; i < length; i++)
		{
			cond = conds.get(i);
			if (i == 0)
			{
				sb.append(cond.getColumn()).append(' ').append(cond.getCompare().getRdbValue()).append(" ? ");
			}
			else
			{
				sb.append(cond.getLogic().getRdbValue()).append(' ').append(cond.getColumn()).append(' ').append(cond.getCompare().getRdbValue()).append(" ? ");
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取条件值
	 * @param conds
	 * @return
	 */
	public static List<Object> getConditionsVal(List<Condition> conds)
	{
		int length = conds.size();
		List<Object> data = new ArrayList<Object>(length);
		for (int i = 0; i < length; i++)
		{
			data.add(conds.get(i).getValue());
		}
		return data;
	}
	
	/**
	 * 获取条件值
	 * @param conds
	 * @return
	 */
	public static List<Object> getConditionsVal(List<Condition> conds, int size)
	{
		List<Object> data = new ArrayList<Object>(size);
		int length = conds.size();
		for (int i = 0; i < length; i++)
		{
			data.add(conds.get(i).getValue());
		}
		return data;
	}
	
	/**
	 * 设置条件值
	 * @param ps
	 * @param conds
	 * @param index
	 */
	public static void setStatementParams(PreparedStatement ps, List<Object> params, int index) throws Exception
	{
		int length = params.size();
		for (int i = 0; i < length; i++)
		{
			ps.setObject(index + i, params.get(i));
		}
	}
	
	/**
	 * 获取选取列字符串
	 * @param columns
	 * @return
	 */
	public static String getSearchColumnStr(String[] columns)
	{
		StringBuilder sb = new StringBuilder(columns.length * 10);
		for (int i = 0; i < columns.length; i++)
		{
			sb.append(columns[i]).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * 获取排序字符串
	 * @param orders
	 */
	public static String getOrdersStr(List<Order> orders)
	{
		int length = orders.size();
		StringBuilder sb = new StringBuilder(10 * length);
		for (int i = 0; i < length; i++)
		{
			sb.append(orders.get(i).toString()).append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * 获取更新字段
	 * @param vals
	 * @return
	 */
	public static String getUpdateField(List<Value> vals)
	{
		int length = vals.size();
		StringBuilder sb = new StringBuilder(10 * length);
		for (int i = 0; i < length; i++)
		{
			sb.append(vals.get(i).getColumn()).append(" = ?,");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * 获取更新值
	 * @param vals
	 * @return
	 */
	public static List<Object> getUpdateVals(List<Value> vals)
	{
		List<Object> data = new ArrayList<Object>(vals.size());
		int length = vals.size();
		for (int i = 0; i < length; i++)
		{
			data.add(vals.get(i).getValue());
		}
		return data;
	}
	
	/**
	 * 获取更新值
	 * @param vals
	 * @return
	 */
	public static List<Object> getUpdateVals(List<Value> vals, int size)
	{
		List<Object> data = new ArrayList<Object>(size);
		int length = vals.size();
		for (int i = 0; i < length; i++)
		{
			data.add(vals.get(i).getValue());
		}
		return data;
	}
	
	/**
	 * 获取Id集合字符串
	 * @param ids
	 * @return
	 */
	public static String getIdStr(List<Long> ids)
	{
		int length = ids.size();
		StringBuilder sb = new StringBuilder(2 * length);
		for (int i = 0; i < length; i++)
		{
			sb.append(ids.get(i)).append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
