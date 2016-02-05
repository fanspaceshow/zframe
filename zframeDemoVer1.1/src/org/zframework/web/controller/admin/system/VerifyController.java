package org.zframework.web.controller.admin.system;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.DecUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.User;

@Controller
@RequestMapping("/admin/verify")
public class VerifyController extends BaseController<Object>{
	/**
	 * 验证当前用户的密码
	 * 一般用于执行关键性操作需要进行密码验证时使用
	 * @param password
	 * @return
	 */
	@RequestMapping(value="/verifyCUserPass",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject verifyCurrentUserPassword(HttpSession session,String password){
		JSONObject jResult = new JSONObject();
		User user = getCurrentUser();
		if(user==null){
			jResult.element("verifyResult", "SessionTimeOut");
		}else{
			DecUtil des = new DecUtil();
			des.genKey(ApplicationCommon.DEC_KEY);
			password = des.getEncString(password);
			if(ObjectUtil.isNotEmpty(password)){
				if(password.equals(user.getPassWord())){
					jResult.element("verifyResult", "AllowAccess");
					session.setAttribute(ApplicationCommon.ALLOW_ACCESS, true);
					session.setAttribute(ApplicationCommon.ALLOW_ACCESS_TIMESPAN, new java.util.Date());
				}else{
					jResult.element("verifyResult", "AccessDenied");
				}
			}else{
				jResult.element("verifyResult", "AccessDenied");
			}
		}
		return jResult;
	}
}
