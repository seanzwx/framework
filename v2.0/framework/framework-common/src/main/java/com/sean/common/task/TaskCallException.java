package com.sean.common.task;

/**
 *
 * @author arksea
 */
public class TaskCallException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public TaskCallException(String name, Throwable ex)
    {
        super(name,ex);
    }

    public TaskCallException(String name)
    {
        super(name);
    }
}