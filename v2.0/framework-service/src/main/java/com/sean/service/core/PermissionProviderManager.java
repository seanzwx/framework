package com.sean.service.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sean.config.core.Config;
import com.sean.config.enums.ConfigEnum;
import com.sean.log.core.LogFactory;
import com.sean.service.annotation.PermissionConfig;
import com.sean.service.entity.PermissionEntity;
import com.sean.service.enums.L;

/**
 * 权限管理
 * @author sean
 */
public final class PermissionProviderManager
{
	private List<PermissionEntity> permTree;
	private Map<Integer, PermissionEntity> perms;

	private static final Logger logger = LogFactory.getLogger(L.Service);

	public PermissionProviderManager(List<Class<?>> clses) throws Exception
	{
		logger.info("PermissionProviderManager start initializing...");

		permTree = new ArrayList<PermissionEntity>();
		perms = new HashMap<Integer, PermissionEntity>();

		int length = clses.size();
		for (int i = 0; i < length; i++)
		{
			this.init(clses.get(i));
		}

		logger.info("PermissionProviderManager start initializing permission tree");

		// 初始化权限树
		for (int i = 0; i < permTree.size(); i++)
		{
			initTree(permTree.get(i));
		}

		logger.info("Permission tree initialized successfully");

		// 开发模式下打印权限树
		if ("dev".equals(Config.getProperty(ConfigEnum.ServiceMode)))
		{
			print();
		}

		logger.info("PermissionProviderManager initialized successfully");
	}

	/**
	 * 初始化权限管理
	 * @param permissionCls
	 */
	private void init(Class<?> permissionCls) throws Exception
	{
		logger.info("PermissionProviderManager load permissions from class " + permissionCls.getName());

		Object obj = permissionCls.newInstance();

		Field[] fs = permissionCls.getDeclaredFields();
		Field f = null;
		for (int i = 0; i < fs.length; i++)
		{
			f = fs[i];
			PermissionConfig pc = f.getAnnotation(PermissionConfig.class);
			if (pc != null)
			{
				int id = f.getInt(obj);

				// 权限值重复
				if (this.perms.containsKey(id))
				{
					throw new RuntimeException("the permission " + f.getName() + "'s value defined repeated in class " + permissionCls.getName()
							+ " try in other ones");
				}
				// 父权限未定义
				if (!this.perms.containsKey(pc.parent()) && (pc.parent() != PermissionProvider.None))
				{
					throw new RuntimeException("the permission " + f.getName() + "'s parent not defined in class " + permissionCls.getName()
							+ ", listene! parent must defined before child in a class");
				}

				PermissionEntity pe = new PermissionEntity(id, pc.parent(), pc.description());
				// 放入所有权限map
				perms.put(id, pe);
				// 如果是顶层权限，首先放入权限树中
				if (pe.getParent() == PermissionProvider.None)
				{
					permTree.add(pe);
				}
			}
		}
	}

	/**
	 * 初始化权限树
	 * @param pe
	 */
	private void initTree(PermissionEntity pe)
	{
		// 加入所有子权限
		for (Iterator<Integer> it = perms.keySet().iterator(); it.hasNext();)
		{
			int id = it.next();
			PermissionEntity item = perms.get(id);
			if (item.getParent() == pe.getId())
			{
				pe.getChildren().add(item);
			}
		}
		// 继续递归
		for (int i = 0; i < pe.getChildren().size(); i++)
		{
			initTree(pe.getChildren().get(i));
		}
	}

	// 打印权限树
	private void print()
	{
		StringBuilder sb = new StringBuilder(4096);
		for (int i = 0; i < permTree.size(); i++)
		{
			dod(sb, permTree.get(i), 0);
		}
		System.out.println(sb.toString());
	}

	// 打印递归方法
	private void dod(StringBuilder sb, PermissionEntity pe, int level)
	{
		String tmp = "";
		for (int j = 0; j < level; j++)
		{
			tmp += "   ";
		}
		sb.append(tmp).append(pe.getId()).append("-").append(pe.getDescription()).append("\n");
		for (int j = 0; j < pe.getChildren().size(); j++)
		{
			dod(sb, pe.getChildren().get(j), level + 1);
		}
	}

}
