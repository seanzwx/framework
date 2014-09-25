package com.sean.common.task;

/**
 * 任务信息
 * @author sean
 */
public class TaskInfo
{
	private String name;
	private boolean daemon = false;
	private int priority;
	private Class<?> clazz;
	private long maxQueueLen = 10;
	private Object args;

	public TaskInfo(String name, Class<?> clazz, Object args)
	{
		this(name, clazz, args, 10, false, Thread.NORM_PRIORITY);
	}

	public TaskInfo(String name, Class<?> clz, Object args, long maxQueueLen, boolean daemon, int priority)
	{
		this.name = name;
		this.clazz = clz;
		this.args = args;
		this.maxQueueLen = maxQueueLen;
		this.daemon = daemon;
		this.priority = priority;
	}

	public Class<?> getClazz()
	{
		return clazz;
	}

	public String getName()
	{
		return name;
	}

	public Object getArgs()
	{
		return args;
	}

	public long getMaxQueueLen()
	{
		return maxQueueLen;
	}

	public boolean isDaemon()
	{
		return daemon;
	}

	public int getPriority()
	{
		return priority;
	}

	@Override
	public String toString()
	{
		return "TaskInfo [name=" + name + ", daemon=" + daemon + ", priority=" + priority + ", clazz=" + clazz + ", maxQueueLen=" + maxQueueLen
				+ ", args=" + args + "]";
	}

}