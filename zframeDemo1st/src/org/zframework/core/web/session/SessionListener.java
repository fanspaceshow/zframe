package org.zframework.core.web.session;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.entity.system.User;
/**
 * session监听器
 * @author ZENGCHAO
 *
 */
public class SessionListener implements HttpSessionListener{
	
	public void sessionCreated(HttpSessionEvent se) {
		//session创建
	}

	public void sessionDestroyed(HttpSessionEvent se) {
		//session失效
		User user = (User) se.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
		if(ObjectUtil.isNotNull(user)){
			ApplicationCommon.LOGIN_USERS.remove(user.getLoginName());
			ApplicationCommon.LOGIN_SESSIONS.remove(user.getLoginName());
		}
	}

}
