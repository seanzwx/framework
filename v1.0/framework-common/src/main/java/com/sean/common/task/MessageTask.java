package com.sean.common.task;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.sean.common.enums.L;
import com.sean.log.core.LogFactory;

/**
 * 基于消息传递的任务
 * @author sean
 */
public abstract class MessageTask extends Task
{
	private static final Logger logger = LogFactory.getLogger(L.Task);

	/**
	 * stopInfo: 退出请求信息，
	 * 当stopInfo等于"normal"时认为是一个正常退出的请求；
	 * 当stopInfo等于其他值时，认为是因为错误而收到的退出请求，stopInfo的值即为错误信息。
	 */
	private volatile String stopInfo = null;
	private long maxQueueLen;
	private final BlockingQueue<QueueMessage> queue = new LinkedBlockingQueue<>();

	public MessageTask(String name, long maxQueueLen, Object args)
	{
		super(name, args);
		this.maxQueueLen = maxQueueLen;
	}

	/**
	 * 异步调用，不等待执行结果
	 * @param msg
	 */
	public final void asyncCall(Message msg)
	{
		try
		{
			QueueMessage tmsg = new QueueMessage(MessageType.ASYNC, msg, Thread.currentThread().getName());
			synchronized (queue)
			{
				if (queue.size() >= maxQueueLen)
				{
					throw new RuntimeException("任务:" + getName() + "'消息队列溢出");
				}
			}
			queue.put(tmsg);
		}
		catch (InterruptedException ex)
		{
			if (!isStopped())
			{
				errorStopRequest("put message to queue interrupted");
			}
		}
	}

	/**
	 * 同步调用，等待执行结果
	 * @param msg
	 * @param result
	 */
	public final void syncCall(Message msg, Result result)
	{
		try
		{
			QueueMessage tmsg = new QueueMessage(MessageType.SYNC, msg, Thread.currentThread().getName(), result);
			synchronized (queue)
			{
				if (queue.size() >= maxQueueLen)
				{
					throw new RuntimeException("任务:" + getName() + "'消息队列溢出");
				}
			}
			queue.put(tmsg);
		}
		catch (InterruptedException ex)
		{
			if (!isStopped())
			{
				errorStopRequest("put message to queue interrupted");
			}
		}
	}

	@Override
	public final void run()
	{
		RuntimeException runtimeEx = null;
		try
		{
			this.init();
			this.listen();
		}
		catch (RuntimeException ex)
		{
			runtimeEx = ex;
		}
		catch (Throwable ex)
		{
			runtimeEx = new RuntimeException(ex);
		}

		if (stopInfo == null)
		{
			// 没有退出请求，根据是否有异常进行处理
			terminate(runtimeEx);
			if (runtimeEx == null)
			{
				stopInfo = "normal";
			}
			else
			{
				stopInfo = runtimeEx.getMessage();
				if (stopInfo == null)
				{
					stopInfo = runtimeEx.getClass().getSimpleName();
				}
				throw runtimeEx;
			}
		}
		else
		{
			if (stopInfo.equals("normal"))
			{
				// 收到正常退出请求，不抛出异常
				terminate(null);
			}
			else
			{
				// 收到错误退出请求
				if (runtimeEx == null)
				{
					runtimeEx = new RuntimeException(stopInfo);
					terminate(runtimeEx);
					throw runtimeEx;
				}
				else
				{
					terminate(runtimeEx);
					throw runtimeEx;
				}
			}
		}
	}

	/**
	 * 一次性读取队列消息，防止多次的加锁
	 * @param msgList
	 */
	private void tack(List<QueueMessage> msgList) throws InterruptedException
	{
		QueueMessage tmsg = queue.take();
		msgList.add(tmsg);
		// 读取队列里面的所有元素到msgList
		queue.drainTo(msgList);
	}

	/**
	 * 监听轮寻消息队列
	 * @throws Throwable
	 */
	private final void listen() throws Exception
	{
		List<QueueMessage> msgList = new LinkedList<>();
		while (!isStopped())
		{
			try
			{
				tack(msgList);
			}
			catch (InterruptedException ex)
			{
				Thread.interrupted();
				if (!isStopped())
				{
					logger.warn("wait message interrupted", ex);
				}
				return;
			}
			// 执行所有队列消息
			for (QueueMessage m : msgList)
			{
				execute(m);
			}
			msgList.clear();
		}
	}

	/**
	 * 执行消息
	 * @param tmsg
	 * @throws Exception
	 */
	private final void execute(QueueMessage tmsg) throws Exception
	{
		if (tmsg.type == MessageType.ASYNC)
		{
			handle_info(tmsg.message, tmsg.from);
		}
		else if (tmsg.type == MessageType.SYNC)
		{
			try
			{
				tmsg.result.msg = handle_call(tmsg.message, tmsg.from);
			}
			catch (Exception ex)
			{
				// 当被调用Task异常退出时，调用者也应抛出异常
				tmsg.result.msg = new ThrowMessage(ex);
				throw ex;
			}
			finally
			{
				synchronized (tmsg.result)
				{
					// 唤醒阻塞调用
					tmsg.result.notify();
				}
			}
		}
	}

	public final void errorStopRequest(String error)
	{
		stopInfo = error;
		this.interrupt();
	}

	public final boolean isStopped()
	{
		return stopInfo != null;
	}

	public final boolean isErrorStopped()
	{
		return stopInfo != null && !stopInfo.equals("normal");
	}

	public final boolean isNormalStopped()
	{
		return stopInfo != null && stopInfo.equals("normal");
	}

	public final String getStopInfo()
	{
		return stopInfo;
	}

	@Override
	public final void normalStopRequest()
	{
		stopInfo = "normal";
		this.interrupt();
	}

	public long getMaxQueueLen()
	{
		return maxQueueLen;
	}

	/**
	 * 初始化
	 * @throws Exception
	 */
	protected abstract void init() throws Exception;

	/**
	 * 异步操作, 无返回值
	 * @param msg
	 * @param from
	 * @throws Throwable
	 */
	protected abstract void handle_info(Message msg, String from) throws Exception;

	/**
	 * 同步操作, 等待执行结果
	 * @param msg
	 * @param from
	 * @return
	 * @throws Throwable
	 */
	protected abstract Message handle_call(Message msg, String from) throws Exception;

	/**
	 * 挂起任务
	 * @param ex
	 */
	protected abstract void terminate(Exception ex);

	private enum MessageType
	{
		/**
		 * 异步
		 */
		ASYNC,
		/**
		 * 同步
		 */
		SYNC
	}

	private class QueueMessage
	{
		public QueueMessage(MessageType type, Message message, String from)
		{
			this(type, message, from, null);
		}

		public QueueMessage(MessageType type, Message message, String from, Result result)
		{
			this.type = type;
			this.message = message;
			this.from = from;
			this.result = result;
		}

		public final MessageType type;
		public final Message message;
		public final String from;
		public final Result result;
	}
}
