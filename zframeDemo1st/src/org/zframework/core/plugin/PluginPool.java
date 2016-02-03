package org.zframework.core.plugin;

import java.util.HashMap;
import java.util.Map;

public class PluginPool {
	private PluginPool(){
		
	}
	private static Map<String,IPlugin> mapPlugins = new HashMap<String, IPlugin>();
	
	public static IPlugin getPluginBean(String name){
		return mapPlugins.get(name);
	}
	
	public static void addPluginBean(String name,IPlugin plugin){
		mapPlugins.put(name, plugin);
	}
}
