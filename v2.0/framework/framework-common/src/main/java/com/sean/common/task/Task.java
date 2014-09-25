package com.sean.common.task;

/**
 * 线程监控子任务类，继承自Thread，由Supervisor启动并注册其为UncaughtExceptionHandler，
 * 当子任务线程异常退出时将会被Supervisor重启。
 * @author sean
 */
public abstract class Task extends Thread
{
	private Object args;

	/**
	 * 构造函数增加子任务用于初始化的参数， 当异常退出被重启时将由Supervisor重新创建并传入
	 */
	public Task(String name, Object args)
	{
		super(name);
		this.args = args;
	}

	public Object getArgs()
	{
		return args;
	}

	public abstract void normalStopRequest();
}
