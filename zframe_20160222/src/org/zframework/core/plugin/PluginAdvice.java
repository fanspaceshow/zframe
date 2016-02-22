package org.zframework.core.plugin;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

public class PluginAdvice implements MethodBeforeAdvice,AfterReturningAdvice{
	private Map<IPlugin,String> beforeAdvices = new LinkedHashMap<IPlugin,String>();
	private Map<IPlugin,String> afterAdvices = new LinkedHashMap<IPlugin,String>();
	public void addBeforeAdvices(IPlugin plugin,String expression){
		this.beforeAdvices.put(plugin,expression);
	}
	public void addAfterAdvices(IPlugin plugin,String expression){
		this.afterAdvices.put(plugin,expression);
	}
	
	public void before(Method method, Object[] params, Object obj)
			throws Throwable {
		for(IPlugin plugin : beforeAdvices.keySet()){
			String exp = beforeAdvices.get(plugin);
			AspectJExpressionPointcut ajp = new AspectJExpressionPointcut();
			ajp.setExpression(exp);
			if(ajp.matches(method,obj.getClass(),params)){
				plugin.before(method, params, obj);
			}
		}
	}
	
	public void afterReturning(Object rv, Method method, Object[] params,
			Object obj) throws Throwable {
		for(IPlugin plugin : afterAdvices.keySet()){
			String exp = afterAdvices.get(plugin);
			AspectJExpressionPointcut ajp = new AspectJExpressionPointcut();
			ajp.setExpression(exp);
			if(ajp.matches(method,obj.getClass(),params)){
				plugin.afterReturning(rv, method, params, obj);
			}
		}
	}
}
