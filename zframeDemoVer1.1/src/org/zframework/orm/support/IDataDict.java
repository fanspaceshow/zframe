package org.zframework.orm.support;

import java.util.List;
import java.util.Map;

import org.zframework.orm.cache.ICacheProvider;

public interface IDataDict extends ICacheProvider{
	
	List<Object> getList(String key);
	
	Map<String,Object> getMap(String key);
	
	void put(String key,Object data);
	
}
