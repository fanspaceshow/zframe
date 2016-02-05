package org.zframework.core.plugin;

import java.lang.reflect.Method;

public interface IPlugin {
	/**
	 * 插件初始化
	 * @return
	 */
	boolean init(String[] args);
	/**
	 * 插件销毁
	 * @return
	 */
	void destory(String[] args);
	/**
	 * 拦截方法 - before
	 * @param method
	 * @param params
	 * @param obj
	 */
	void before(Method method,Object[] params,Object obj);
	/**
	 * 拦截方法 - after
	 * @param returnValue
	 * @param method
	 * @param params
	 * @param obj
	 */
	void afterReturning(Object returnValue,Method method,Object[] params,Object obj);
	
}
