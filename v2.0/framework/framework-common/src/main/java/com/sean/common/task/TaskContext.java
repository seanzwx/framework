package com.sean.common.task;

/**
 * 任务上下文
 * @author sean
 */
public class TaskContext
{
	private static final TaskManager taskManager = new TaskManager();

	/**
	 * 启动任务
	 * @param childs
	 */
	public static void startTask(TaskInfo taskinfo)
	{
		taskManager.startTask(taskinfo);
	}

	/**
	 * 停止所有任务
	 */
	public static void stopAllTask()
	{
		taskManager.stopAllTask();
	}

	/**
	 * 停止任务
	 * @param taskName
	 */
	public static void stopTask(String taskName)
	{
		taskManager.stopTask(taskName);
	}

	/**
	 * 发送消息给目标任务，这种调用不会阻塞调用者，也不会阻塞被调用者
	 * @param dest
	 * @param msg
	 */
	public static void asyncCall(String dest, Message msg)
	{
		MessageTask task = (MessageTask) taskManager.getTask(dest);
		if (task != null)
		{
			task.asyncCall(msg);
		}
		else
		{
			throw new TaskCallException("任务" + dest + "不存在, 发送消息:" + msg);
		}
	}

	/**
	 * 发送消息给目标任务，要求其处理请求后返回处理结果，这种调用会阻塞调用者，也会阻塞被调用者
	 * @param dest
	 * @param callmsg
	 * @return
	 */
	public static Message syncCall(String dest, Message callmsg)
	{
		return syncCall(dest, callmsg, Long.MAX_VALUE);
	}

	/**
	 * 发送消息给目标任务，要求其处理请求后返回处理结果，这种调用会阻塞调用者，也会阻塞被调用者
	 * @param dest
	 * @param msg
	 * @param timeout
	 * @return
	 */
	public static Message syncCall(String dest, Message msg, long timeout)
	{
		MessageTask task = (MessageTask) taskManager.getTask(dest);
		if (task != null)
		{
			Result result = new Result();
			synchronized (result)
			{
				task.syncCall(msg, result);
				try
				{
					// 等待结果
					result.wait(timeout);
				}
				catch (InterruptedException e)
				{
					throw new TaskCallException("同步请求任务" + dest + "超时" + timeout + ":" + msg, e);
				}
			}
			// 执行异常
			if (result.msg != null && (result.msg instanceof ThrowMessage))
			{
				Object v = result.msg.getValue();
				throw new TaskCallException("请求任务" + dest + "异常", (Exception) v);
			}
			else
			{
				return result.msg;
			}
		}
		else
		{
			throw new TaskCallException("任务" + dest + "不存在, 发送消息:" + msg);
		}
	}
}
