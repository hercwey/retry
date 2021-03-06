/**
 * 
 */
package com.github.xjs1919.retry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.xjs1919.retry.persist.PersistService;

/**
 * @author 605162215@qq.com
 *
 * 2016��8��6�� ����2:43:54
 */
public class RetryPersistService implements PersistService<RetryTask>{
	
	private ConcurrentHashMap<String, String> db = new ConcurrentHashMap<String, String>();
	
	@Override
	public void save(RetryTask task) {
		System.out.println("[RetryPersistService]save:"+task);
		String uuid = task.getUuid();
		int interval = task.getInterval();
		db.put(uuid, "{\"clazz\":\""+task.getTask().getName()+"\",\"interval\":\""+interval+"\", \"id\":\""+task.getUuid()+"\", \"index\":"+task.getIndex()+"}");
	}
	
	@Override
	public void delete(RetryTask task) {
		System.out.println("[RetryPersistService]delete:"+task);
		String uuid = task.getUuid();
		db.remove(uuid);
	}
	@Override
	public void update(RetryTask task){
		System.out.println("[RetryPersistService]update:"+task);
		String uuid = task.getUuid();
		db.remove(uuid);
		save(task);
	}
	@Override
	public void deleteLogic(RetryTask task) {
		delete(task);
	}
	@Override
	public List<RetryTask> getAll() {
		
		RetryTask oldTask = new RetryTask("uuid",0,0,Main.Business.class, "old param");
		db.put(oldTask.getUuid(),  "{"
								+ "\"clazz\":\""+oldTask.getTask().getName()+"\","
								+ "\"param\":\""+oldTask.getParam()+"\","
								+ "\"interval\":\""+oldTask.getInterval()+"\", "
								+ "\"id\":\""+oldTask.getUuid()+"\", "
								+ "\"index\":"+oldTask.getIndex()+"}");
		
		System.out.println("[RetryPersistService]getAll");
		List<RetryTask> list = new ArrayList<RetryTask>();
		for(Map.Entry<String, String> entry : db.entrySet()){
			String json = entry.getValue();
			JSONObject jo = JSON.parseObject(json);
			String clazzName = jo.getString("clazz");
			int interval = jo.getIntValue("interval");
			String id = jo.getString("id");
			int index = jo.getIntValue("index");
			String param = jo.getString("param");
			RetryTask task = new RetryTask(id, index, interval, getClass(clazzName), param);
			list.add(task);
		}
		return list;
	}
	
	@Override
	public int size(){
		return db.size();
	}
	
	@SuppressWarnings("unchecked")
	private Class<? extends RetryAble> getClass(String className){
		try{
			return (Class<? extends RetryAble>)Class.forName(className);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
