package com.sean.service.entity;

import com.sean.service.core.Version;
import com.sean.service.enums.ReturnType;
import com.sean.service.worker.Worker;

/**
 * Action实体
 * @author sean
 *
 */
public class ActionEntity
{
	private boolean transation;
	private ParameterEntity[] mustParams;
	private ParameterEntity[] optionalParams;
	private ReturnParameterEntity[] returnParams;
	private int permission;
	private boolean authenticate;
	private ReturnType returnType;
	private String description;
	private Version version;
	private Class<?> cls;
	private Worker worker;

	public ActionEntity(boolean transation, ReturnParameterEntity[] returnParams, ParameterEntity[] mustParams,
			ParameterEntity[] optionalParams, int permission, boolean authenticate, ReturnType returnType, Class<?> cls,
			String description, Version version, Worker worker)
	{
		this.transation = transation;
		this.returnParams = returnParams;
		this.mustParams = mustParams;
		this.optionalParams = optionalParams;
		this.permission = permission;
		this.authenticate = authenticate;
		this.returnType = returnType;
		this.cls = cls;
		this.description = description;
		this.version = version;
		this.worker = worker;
	}

	public boolean isTransation()
	{
		return transation;
	}

	public ReturnParameterEntity[] getReturnParams()
	{
		return returnParams;
	}

	public ParameterEntity[] getMustParams()
	{
		return mustParams;
	}

	public ParameterEntity[] getOptionalParams()
	{
		return optionalParams;
	}

	public int getPermission()
	{
		return permission;
	}

	public ReturnType getReturnType()
	{
		return returnType;
	}

	public Class<?> getCls()
	{
		return cls;
	}

	public boolean isAuthenticate()
	{
		return authenticate;
	}

	public String getDescription()
	{
		return description;
	}

	public Version getVersion()
	{
		return version;
	}

	public Worker getWorker()
	{
		return worker;
	}

}
