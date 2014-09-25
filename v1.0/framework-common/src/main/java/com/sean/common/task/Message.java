package com.sean.common.task;

/**
 * 线程通信消息
 * @author sean
 */
public class Message
{
	private String name;
	private Object value;
	
	public Message(String name, Object value)
	{
		this.name = name;
		this.value = value;
	}

	public String getName()
	{
		return name;
	}

	public Object getValue()
	{
		return value;
	}

	@Override
	public String toString()
	{
		return "Message [name=" + name + ", value=" + value + "]";
	}

}
