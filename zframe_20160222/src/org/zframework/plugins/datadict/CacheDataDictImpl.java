package org.zframework.plugins.datadict;

import java.util.List;
import java.util.Map;

import org.zframework.orm.support.IDataDict;
/**
 * 缓存数据字典类
 * @author zengchao
 *
 */
public class CacheDataDictImpl implements IDataDict{

	@Override
	public Object get(String key) {
		// TODO Auto-generated method stub
		return "2";
	}

	@Override
	public boolean sync() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Object> getList(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getMap(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(String key, Object data) {
		// TODO Auto-generated method stub
		
	}

}
