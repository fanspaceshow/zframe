package org.zframework.core.web.session;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.entity.system.User;

/**
 * session属性编辑器
 * @author ZENGCHAO
 *
 */
public class SessionAttributeListener implements HttpSessionAttributeListener {

	public void attributeAdded(HttpSessionBindingEvent se) {
		if(ApplicationCommon.SESSION_ADMIN.equals(se.getName())){
			if(ObjectUtil.isNotNull(se.getValue())){
				User user = (User) se.getValue();
				ApplicationCommon.LOGIN_USERS.add(user.getLoginName());
			}
			
		}
	}

	public void attributeRemoved(HttpSessionBindingEvent se) {
		if(ApplicationCommon.SESSION_ADMIN.equals(se.getName())){
			if(ObjectUtil.isNotNull(se.getValue())){
				User user = (User) se.getValue();
				ApplicationCommon.LOGIN_USERS.remove(user.getLoginName());
			}
		}
	}

	public void attributeReplaced(HttpSessionBindingEvent se) {
		if(ApplicationCommon.SESSION_ADMIN.equals(se.getName())){
			if(ObjectUtil.isNotNull(se.getValue())){
				User user = (User) se.getValue();
				ApplicationCommon.LOGIN_USERS.remove(user.getLoginName());
				ApplicationCommon.LOGIN_USERS.add(user.getLoginName());
			}
		}
	}

}
