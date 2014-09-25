package com.sean.common.task;

public class Main
{
	public static void main(String[] args) throws InterruptedException
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				TaskContext.startTask(new TaskInfo("t1", T1.class, "hello"));
			}
		});
		t.start();
		t.join();

		TaskContext.asyncCall("t1", new Message("key", "main"));
		System.out.println("发送异步消息");
		TaskContext.asyncCall("t1", new Message("key", "main"));
		System.out.println("发送异步消息");

		try
		{
			System.out.println(TaskContext.syncCall("t1", new Message("key", "main")));
			System.out.println("发送同步消息");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		Thread.sleep(1000);
		System.out.println("重启后");
		//
		// try
		// {
		// System.out.println(TaskContext.syncCall("t1", new Message("key",
		// "main")));
		// System.out.println("发送同步消息");
		// }
		// catch(Exception e)
		// {
		// e.printStackTrace();
		// }
		TaskContext.stopAllTask();
	}

	public static class T1 extends MessageTask
	{
		public T1(String name, long maxQueueLen, String args)
		{
			super(name, maxQueueLen, args);
		}

		@Override
		protected void init() throws Exception
		{
		}

		@Override
		protected void handle_info(Message msg, String from) throws Exception
		{
			System.out.println("T1收到异步消息:" + msg);
			Thread.sleep(1000);
			System.out.println("t1执行异步完成");
		}

		@Override
		protected Message handle_call(Message msg, String from) throws Exception
		{
			// throw new RuntimeException("执行异常");
			System.out.println("T1收到同步消息:" + msg);
			return new Message("rs", "sean");
		}

		@Override
		protected void terminate(Exception ex)
		{
		}
	}
}
