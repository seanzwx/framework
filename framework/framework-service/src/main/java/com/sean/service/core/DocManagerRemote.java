//package com.sean.service.core;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.sean.doc.api.DocManager;
//import com.sean.persist.annotation.ColumnConfig;
//import com.sean.persist.core.PersistContext;
//import com.sean.persist.dictionary.Dictionary;
//import com.sean.persist.entity.DictionaryKeyEntity;
//import com.sean.service.entity.ActionEntity;
//import com.sean.service.entity.ReturnParameterEntity;
//import com.sean.service.entity.UseDicEntity;
//import com.sean.service.enums.Format;
//
///**
// * 接口文档remote
// * @author sean
// */
//public class DocManagerRemote implements DocManager
//{
//	@Override
//	public CharSequence inquireAction(CharSequence actionName)
//	{
//		ActionEntity action = FrameworkContext.CTX.getAction(null, actionName.toString()).getActionEntity();
//		String tmp = JSON.toJSONString(action);
//		JSONObject obj = JSON.parseObject(tmp);
//		this.getComment(action, obj);
//		return "{\"data\":" + obj.toString() + "}";
//	}
//
//	@Override
//	public CharSequence inquireActionList()
//	{
//		List<Action> acts = FrameworkContext.CTX.getAllActions();
//		List<ActionEntity> actions = new ArrayList<ActionEntity>(acts.size());
//		for (Action a : acts)
//		{
//			actions.add(a.getActionEntity());
//		}
//		return "{\"data\":" + JSON.toJSONString(actions) + "}";
//	}
//
//	private void getComment(ActionEntity action, JSONObject json)
//	{
//		ReturnParameterEntity[] rets = action.getReturnParams();
//		int index = -1;
//		for (ReturnParameterEntity param : rets)
//		{
//			index++;
//			// 如果是实体
//			if (param.getFormat() == Format.Entity || param.getFormat() == Format.EntityList)
//			{
//				int length = param.getFields().length;
//				String field;
//				for (int i = 0; i < length; i++)
//				{
//					field = param.getFields()[i];
//					String descr = "";
//					// 在实体种查找
//					try
//					{
//						Field f = param.getEntity().getDeclaredField(field);
//						ColumnConfig cc = f.getAnnotation(ColumnConfig.class);
//						if (cc != null)
//						{
//							descr = cc.description();
//						}
//					}
//					catch (NoSuchFieldException e)
//					{
//						// 在字典种查找
//						UseDicEntity[] dics = param.getDics();
//						for (int j = 0; j < dics.length; j++)
//						{
//							Dictionary dic = PersistContext.CTX.getDictionary(dics[j].getDic());
//							DictionaryKeyEntity[] keys = dic.getEntity().getKeys();
//							boolean isFound = false;
//							for (int k = 0; k < keys.length; k++)
//							{
//								isFound = false;
//								if (keys[k].getKey().equals(field))
//								{
//									descr = keys[k].getDescription();
//									isFound = true;
//									break;
//								}
//								if (isFound)
//								{
//									break;
//								}
//							}
//						}
//					}
//					JSONObject item = (JSONObject) json.getJSONArray("returnParams").get(index);
//					item.getJSONArray("fields").set(i, field + ":" + descr);
//				}
//			}
//		}
//	}
//}
