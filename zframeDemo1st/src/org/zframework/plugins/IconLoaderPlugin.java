package org.zframework.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.zframework.core.plugin.IPlugin;
import org.zframework.core.plugin.annotation.Plugin;
import org.zframework.core.support.ApplicationCommon;

@Plugin("IconLoader")
public class IconLoaderPlugin implements IPlugin {

	@Override
	public boolean init(String[] args) {
		//获取文件路径
		String iconCss = new File(CommonLoaderPlugin.class.getResource("/").getFile()).getParentFile().getParent()+"/resources/framework/css/easyui/icon.css";
		File fIconCss = new File(iconCss);
		if(fIconCss.exists()){
			ApplicationCommon.ICONCLS_LIST = new ArrayList<String>();
			try {
				BufferedReader br = new BufferedReader(new FileReader(fIconCss));
				while(br.ready()){
					String line = br.readLine();
					if(line.startsWith(".icon")){
						String iconCls = line.substring(0,line.indexOf("{")).replace(".", "").trim();
						ApplicationCommon.ICONCLS_LIST.add(iconCls);
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Collections.sort(ApplicationCommon.ICONCLS_LIST);
		Logger.getLogger(this.getClass()).info("Load icon resources Complete.");
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
