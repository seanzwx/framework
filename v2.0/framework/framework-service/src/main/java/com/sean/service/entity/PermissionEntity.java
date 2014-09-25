package com.sean.service.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限实体
 * 
 * @author Sean
 * 
 */
public class PermissionEntity
{
	private int id;
	private String description;
	private int parent;
	private List<PermissionEntity> children;

	public PermissionEntity(int id, int parent, String description)
	{
		this.id = id;
		this.parent = parent;
		this.description = description;
		children = new ArrayList<PermissionEntity>();
	}

	public int getId()
	{
		return id;
	}

	public String getDescription()
	{
		return description;
	}

	public int getParent()
	{
		return parent;
	}

	public List<PermissionEntity> getChildren()
	{
		return children;
	}

}
