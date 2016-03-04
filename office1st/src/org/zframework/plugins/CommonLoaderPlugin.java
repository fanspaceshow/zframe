package org.zframework.plugins;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.zframework.core.plugin.IPlugin;
import org.zframework.core.plugin.annotation.Plugin;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.web.controller.admin.system.ServerController;
import org.zframework.web.service.admin.system.CommonService;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.SafeConfigService;
import org.zframework.web.service.admin.system.SafeIpService;

/**
 * 加载数据库的数据到内存中
 * @author ZENGCHAO
 *
 */
@Plugin("commonLoader")
public class CommonLoaderPlugin implements IPlugin{
	@Autowired
	private CommonService commonService;
	@Autowired
	private SafeIpService safeIpService;
	@Autowired
	private SafeConfigService scService;
	@Autowired
	private LogService logService;
	
	public boolean init(String[] args) {
		//判断是否是web容器加载完成后
		if(ApplicationCommon.SYSCOMMONS.size()==0){
			//初始化数据字典
			commonService.InitCommons();
			//初始化IP地址
			safeIpService.initIp();
			//初始化IP规则
			scService.initSafeConfig();
			//记录启动时间
			ServerController.startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			logService.recordInfo("SYSTEM", "System CommonParam Initialized", "SYSTEM", "SYSTEM");
		}
		return true;
	}
	public void destory(String[] args) {
	}
	public void before(Method method, Object[] params, Object obj) {
		
	}
	public void afterReturning(Object returnValue, Method method, Object[] params,
			Object obj) {
		
	}
}
