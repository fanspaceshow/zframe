package org.zframework.web.controller.admin.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/sessionid")
public class SessionIdController {
	
@RequestMapping(value="/getid",method={RequestMethod.GET,RequestMethod.POST})
@ResponseBody
public String[] getSessionid(HttpServletRequest request,HttpServletResponse response){
	String sessionId=request.getSession().getId();
	
	String[] str = {"sessionid",sessionId};
	return str;
}
}
