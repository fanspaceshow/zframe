package org.zframework.plugins.datadict;

import java.lang.reflect.Method;

import org.zframework.core.plugin.IPlugin;
import org.zframework.core.plugin.annotation.Plugin;

@Plugin("DataDictLoader")
public class DataDictLoaderPlugin implements IPlugin{
	
	@Override
	public boolean init(String[] args) {
		//ApplicationCommon.DATADICT = new CacheDataDictImpl();
		return true;
	}

	@Override
	public void destory(String[] args) {
		
	}

	@Override
	public void before(Method method, Object[] params, Object obj) {
		
	}

	@Override
	public void afterReturning(Object returnValue, Method method,
			Object[] params, Object obj) {
		
	}
}
