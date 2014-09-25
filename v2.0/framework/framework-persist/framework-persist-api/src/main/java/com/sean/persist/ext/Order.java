package com.sean.persist.ext;

import com.sean.persist.enums.OrderEnum;

/**
 * 排序对象
 * 
 * @author sean
 * 
 */
public final class Order
{
	private String column;
	private OrderEnum order;

	private Order()
	{
	}

	/**
	 * 排序构造
	 * @param column				数据库列
	 * @param order					排序
	 */
	public Order(String column, OrderEnum order)
	{
		this();
		this.column = column;
		this.order = order;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(20);
		sb.append(this.column).append(" ").append(this.order.getRdbValue());
		return sb.toString();
	}

	public String getColumn()
	{
		return column;
	}

	public OrderEnum getOrder()
	{
		return order;
	}

}
