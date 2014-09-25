package com.sean.service.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sean.persist.annotation.ColumnConfig;
import com.sean.persist.core.PersistContext;
import com.sean.persist.dictionary.Dictionary;
import com.sean.persist.entity.DictionaryKeyEntity;
import com.sean.service.action.InquireApiAction;
import com.sean.service.action.InquireApiListAction;
import com.sean.service.action.LoadCssAction;
import com.sean.service.action.LoadJsAction;
import com.sean.service.entity.ActionEntity;
import com.sean.service.entity.ReturnParameterEntity;
import com.sean.service.entity.UseDicEntity;
import com.sean.service.enums.Format;

/**
 * 接口文档
 * @author sean
 */
public class DocManager
{
	public static String inquireAction(String actionName, String version)
	{
		ActionEntity action = FrameworkContext.CTX.getAction(version, actionName.toString()).getActionEntity();
		JSONObject obj = new JSONObject();
		obj.put("action", action.getCls().getSimpleName());
		obj.put("descr", action.getDescription());
		obj.put("version", action.getVersion().getVersion());
		obj.put("transation", action.isTransation());
		obj.put("auth", action.isAuthenticate());
		obj.put("returnType", action.getReturnType());
		obj.put("permission", action.getPermission());
		obj.put("clazz", action.getCls().getName());
		obj.put("mustParams", action.getMustParams());
		obj.put("optionalParams", action.getOptionalParams());
		getComment(action, obj);
		return obj.toJSONString();
	}

	public static String inquireActionList() throws Exception
	{
		List<Action> acts = FrameworkContext.CTX.getAllActions();
		List<JSONObject> actions = new ArrayList<>(acts.size());
		for (Action a : acts)
		{
			// 过滤框架内部接口
			if (a.getActionEntity().getCls() == LoadCssAction.class)
			{
				continue;
			}
			if (a.getActionEntity().getCls() == LoadJsAction.class)
			{
				continue;
			}
			if (a.getActionEntity().getCls() == InquireApiAction.class)
			{
				continue;
			}
			if (a.getActionEntity().getCls() == InquireApiListAction.class)
			{
				continue;
			}

			JSONObject act = new JSONObject();
			act.put("action", a.getActionEntity().getCls().getSimpleName());
			act.put("descr", a.getActionEntity().getDescription());
			act.put("module", ((Module) a.getActionEntity().getModule().newInstance()).getName());
			act.put("version", a.getActionEntity().getVersion().getVersion());
			actions.add(act);
		}
		return JSON.toJSONString(actions);
	}

	protected static void getComment(ActionEntity action, JSONObject json)
	{
		JSONArray list = new JSONArray();
		ReturnParameterEntity[] rets = action.getReturnParams();
		for (ReturnParameterEntity param : rets)
		{
			JSONObject obj = new JSONObject();
			obj.put("name", param.getName());
			obj.put("description", param.getDescription());
			obj.put("format", param.getFormat());

			// 如果是实体
			if (param.getFormat() == Format.Entity || param.getFormat() == Format.EntityList || param.getFormat() == Format.Table)
			{
				obj.put("entity", param.getEntity().getSimpleName());

				JSONArray fields = new JSONArray();
				int length = param.getFields().length;
				String field;
				for (int i = 0; i < length; i++)
				{
					field = param.getFields()[i];
					String descr = "";
					// 在实体种查找
					try
					{
						Field f = param.getEntity().getDeclaredField(field);
						ColumnConfig cc = f.getAnnotation(ColumnConfig.class);
						if (cc != null)
						{
							descr = cc.descr();
						}
					}
					catch (NoSuchFieldException e)
					{
						// 在字典种查找
						UseDicEntity[] dics = param.getDics();
						for (int j = 0; j < dics.length; j++)
						{
							Dictionary dic = PersistContext.CTX.getDictionary(dics[j].getDic());
							DictionaryKeyEntity[] keys = dic.getEntity().getKeys();
							boolean isFound = false;
							for (int k = 0; k < keys.length; k++)
							{
								isFound = false;
								if (keys[k].getKey().equals(field))
								{
									descr = keys[k].getDescription();
									isFound = true;
									break;
								}
								if (isFound)
								{
									break;
								}
							}
						}
					}
					JSONObject f = new JSONObject();
					f.put("name", field);
					f.put("descr", descr);
					fields.add(f);
				}
				obj.put("fields", fields);
			}
			// avro 远程实体
			else if (param.getFormat() == Format.AvroEntity || param.getFormat() == Format.AvroEntityList)
			{
				JSONArray fields = new JSONArray();
				int length = param.getFields().length;
				String field;
				for (int i = 0; i < length; i++)
				{
					field = param.getFields()[i];
					JSONObject f = new JSONObject();
					f.put("name", field);
					f.put("descr", "");
					fields.add(f);
				}
				obj.put("fields", fields);
			}

			list.add(obj);
		}
		json.put("returnParams", list);
	}
}
