package com.sean.common.task;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.common.enums.L;
import com.sean.log.core.LogFactory;

/**
 * 任务管理，负责任务的创建，销毁，重启
 * @author sean
 */
public final class TaskManager implements Thread.UncaughtExceptionHandler
{
	private static final Logger logger = LogFactory.getLogger(L.Task);

	private Map<String, Task> taskMap = new HashMap<>();
	private Map<String, TaskInfo> taskinfoMap = new HashMap<>();

	/**
	 * 启动任务
	 * @param taskinfo
	 */
	protected synchronized void startTask(TaskInfo taskinfo)
	{
		if (taskMap.get(taskinfo.getName()) == null)
		{
			Task task = this.createTask(taskinfo);
			task.start();
			logger.debug("启动任务: " + taskinfo.getName());
		}
		else
		{
			logger.warn("任务" + taskinfo.getName() + "已经运行");
		}
	}

	/**
	 * 停止任务
	 * @param taskName
	 */
	protected synchronized void stopTask(String taskName)
	{
		Task task = taskMap.remove(taskName);
		if (task != null)
		{
			task.normalStopRequest();
			logger.debug("停止任务:" + taskName);
		}
	}

	/**
	 * 停止全部任务
	 */
	protected synchronized void stopAllTask()
	{
		for (String taskName : taskinfoMap.keySet())
		{
			this.stopTask(taskName);
		}
	}

	/**
	 * 读取任务
	 * @param taskName
	 * @return
	 */
	protected synchronized Task getTask(String taskName)
	{
		return this.taskMap.get(taskName);
	}

	/**
	 * 创建任务
	 * @param info
	 * @return
	 */
	private synchronized Task createTask(TaskInfo info)
	{
		try
		{
			Constructor<?> con = info.getClazz().getConstructor(String.class, long.class, info.getArgs().getClass());
			Task task = (Task) con.newInstance(info.getName(), info.getMaxQueueLen(), info.getArgs());
			task.setDaemon(info.isDaemon());
			task.setPriority(info.getPriority());
			task.setUncaughtExceptionHandler(this);

			taskinfoMap.put(info.getName(), info);
			taskMap.put(info.getName(), task);

			logger.debug("创建任务: " + info);
			return task;
		}
		catch (Exception e)
		{
			throw new RuntimeException("创建任务" + info + "异常: " + e.getMessage(), e);
		}
	}

	/**
	 * 重启任务
	 * @param taskName
	 */
	private synchronized void restart(String taskName)
	{
		taskMap.remove(taskName);
		TaskInfo taskinfo = taskinfoMap.get(taskName);
		if (taskinfo != null)
		{
			logger.debug("开始重启任务: " + taskName);
			Task task = createTask(taskinfo);
			task.start();
			logger.debug("成功重启任务: " + taskName);
		}
	}

	@Override
	public void uncaughtException(Thread t, Throwable ex)
	{
		String name = t.getName();
		logger.error("任务" + name + "停止，由于执行异常:" + ex.getMessage(), ex);
		// TODO restart
		restart(name);
	}
}
