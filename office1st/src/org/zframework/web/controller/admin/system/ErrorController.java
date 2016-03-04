package org.zframework.web.controller.admin.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zframework.web.controller.BaseController;
import org.zframework.web.service.admin.system.LogService;

/**
 * 错误处理控制类
 * @author ZENGCHAO
 *
 */
@Controller
@RequestMapping("/admin/error")
public class ErrorController extends BaseController<Object>{
	@Autowired
	private LogService logService;
	
	@RequestMapping("/e/{errorCode}")
	public String processingErrors(Exception e,@PathVariable String errorCode){
		logService.recordError("Servlet错误","错误代码:"+errorCode+"msg:"+e.getMessage(),getCurrentUser().getLoginName() , getRequestAddr());
		return "redirect:/admin/error/processingErrors/"+errorCode;
	}
	@RequestMapping("/processingErrors/{errorCode}")
	public String processingErrors2(@PathVariable String errorCode){
		return "_error/"+errorCode;
	}
}
