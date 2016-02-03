package org.zframework.web.support;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.zframework.core.util.ObjectUtil;
import org.zframework.orm.support.IDataDict;
import org.zframework.web.service.admin.system.DataDictService;
import org.zframework.web.service.admin.system.LogService;

/**
 * 数据字典默认实现类
 * @author zengchao
 *
 */
public class DataDictDefaultImpl implements IDataDict{
	private Map<String,Object> data = new LinkedHashMap<String, Object>();
	private boolean isLoaded = false;
	@Autowired
	private DataDictService ddService;
	@Autowired
	private LogService logService;
	
	@Override
	public Object get(String key) {
		if(!isLoaded)
			this.sync();
		return data.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public synchronized boolean sync() {
		List<?> lstData = ddService.list("select dataDictType.dataType,dataDictType.name,name,value from DataDict order by dataDictType.id asc,location asc");
		data.clear();
		for(Object dd : lstData){
			Object[] array = (Object[]) dd;
			String dataType = array[0].toString();
			String dataName = array[1].toString();
			if(dataType.equals("MAP")){
				Object obj = data.get(dataName);
				Map<String, String> map = null;
				if(ObjectUtil.isNotNull(obj)){
					map = (Map<String, String>) obj;
				}else{
					map = new LinkedHashMap<String, String>();
				}
				map.put(array[2]+"", array[3]+"");
				data.put(dataName, map);
			}else if(dataType.equals("LIST")){
				Object obj = data.get(dataName);
				List<String> list = null;
				if(ObjectUtil.isNotNull(obj)){
					list = (List<String>) obj;
				}else{
					list = new LinkedList<String>();
				}
				list.add(array[3]+"");
				data.put(dataName, list);
			}else if(dataType.equals("VALUE")){
				data.put(dataName, array[3]);
			}
		}
		isLoaded = true;
		logService.recordInfo("数据字典", "数据字典同步成功", "SYSTEM", "SYSTEM");
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> getList(String key) {
		if(!isLoaded)
			this.sync();
		
		return (List<Object>) data.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getMap(String key) {
		if(!isLoaded)
			this.sync();
		return (Map<String, Object>) data.get(key);
	}

	@Override
	public void put(String key, Object data) {
		if(!isLoaded)
			this.sync();
		
	}

}
