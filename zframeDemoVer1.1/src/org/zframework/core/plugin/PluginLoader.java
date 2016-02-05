package org.zframework.core.plugin;

import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.zframework.core.plugin.PluginAdvice;
import org.zframework.core.plugin.annotation.Plugin;
import org.zframework.core.plugin.annotation.PluginAfterAdvice;
import org.zframework.core.plugin.annotation.PluginBeforeAdvice;
import org.zframework.core.support.ApplicationContextHelper;
import org.zframework.core.util.ClassUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.StringUtil;

public class PluginLoader{
	private Logger logger = Logger.getLogger(this.getClass());
	public void init() {
		Set<Class<?>> classes = ClassUtil.getClassesByAnnotation("org.zframework.plugins",Plugin.class);
		Iterator<Class<?>> iter = classes.iterator();
		while(iter.hasNext()){
			Class<?> cls = iter.next();
			Plugin plu = cls.getAnnotation(Plugin.class);
			if(!plu.enable())//判断插件是否启用
				continue;
			if(ObjectUtil.isNull(plu.value()) || StringUtil.isEmpty(plu.value())){
				logger.info("插件未加载:插件名称不可为空");
				continue;
			}
			boolean isPlugin = false;
			for(Class<?> classType : cls.getInterfaces()){
				if(classType == IPlugin.class){
					isPlugin = true;
					break;
				}
			}
			if(!isPlugin){
				logger.info(plu.value()+"插件未加载:必须实现"+IPlugin.class.getName()+"接口!");
				continue;
			}
			IPlugin plugin = PluginRegister.register(plu.value(), cls, plu.params());
			if(ObjectUtil.isNotNull(plugin)){
				PluginPool.addPluginBean(plu.value(), plugin);
			}
			PluginBeforeAdvice beforeAdvice = cls.getAnnotation(PluginBeforeAdvice.class);
			//拦截规则  - before
			if(ObjectUtil.isNotNull(beforeAdvice)){
				if(!StringUtil.isEmpty(beforeAdvice.expression())){
					PluginAdvice pAdvice = ApplicationContextHelper.getInstance().getBean(PluginAdvice.class);
					pAdvice.addBeforeAdvices(plugin, beforeAdvice.expression());
				}
			}
			PluginAfterAdvice afterAdvice = cls.getAnnotation(PluginAfterAdvice.class);
			//拦截规则  - after
			if(ObjectUtil.isNotNull(afterAdvice)){
				if(!StringUtil.isEmpty(afterAdvice.expression())){
					PluginAdvice pAdvice = ApplicationContextHelper.getInstance().getBean(PluginAdvice.class);
					pAdvice.addAfterAdvices(plugin, afterAdvice.expression());
				}
			}
			logger.debug("Plugin(name:"+plu.value()+",class:"+cls.getName()+") Initialized!");
		}
	}
}
