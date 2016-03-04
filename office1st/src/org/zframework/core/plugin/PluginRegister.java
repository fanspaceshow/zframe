package org.zframework.core.plugin;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.zframework.core.support.ApplicationContextHelper;
import org.zframework.core.util.ObjectUtil;

/**
 * 插件注册类
 * @author zengchao
 *
 */
public class PluginRegister {
	private static Logger logger = Logger.getLogger(PluginRegister.class);
	/**
	 * 
	 * @param 插件名称
	 * @param 插件类
	 * @param 初始化参数
	 * @return
	 */
	public static IPlugin register(String name,Class<?> clazz,String[] args){
		IPlugin plugin = null;
		try {
			plugin = (IPlugin) ApplicationContextHelper.getInstance().getApplicationContext().getAutowireCapableBeanFactory().createBean(clazz);
		} catch (BeansException e) {
			e.printStackTrace();
			logger.error("Plugin Unable to create bean:"+clazz.getName());
			logger.error("Plugin:"+e.getMessage());
		} catch (IllegalStateException e) {
			e.printStackTrace();
			logger.error("Plugin:"+e.getMessage());
		}
		if(ObjectUtil.isNotNull(plugin))
			if(plugin.init(args)){
				return plugin;
			}else{
				return null;
			}
		else
			return null;
	}
}
