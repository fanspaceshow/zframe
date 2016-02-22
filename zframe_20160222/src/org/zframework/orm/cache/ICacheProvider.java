package org.zframework.orm.cache;

public interface ICacheProvider {
	/**
	 * 根据key获取数据字典中的数据
	 * @param key
	 * @return
	 */
	Object get(String key);
	/**
	 * 存放数据
	 * @param key
	 * @param obj
	 */
	void put(String key,Object obj);
	/**
	 * 同步缓存
	 * @return
	 */
	boolean sync();
}
