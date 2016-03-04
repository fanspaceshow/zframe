package org.zframework.web.controller.admin.system;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;



import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Log;
import org.zframework.web.service.admin.system.LogService;


/**
 *@author YangKun
 *@time 2012-12-17 下午2:39:20
 */
@Controller
@RequestMapping("/admin/log")
public class LogController extends BaseController<Log> {
	@Autowired
	private LogService logService;
	/**
	 * 日志首页
	 * @param model
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		logService.recordInfo("查询日志", "成功", getCurrentUser().getLoginName(), getRequestAddr());
		return "admin/system/log/index";
	}
	@RequestMapping(value="/logList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> logList(PageBean pageBean,String name,String value){
		return logService.getLogList(pageBean, name, value);
	}
	
	/***
	 * 删除日志
	 * @param request
	 * @param unit 
	 * @param result
	 * @return
	 * */
	@RequestMapping(value="/doDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpServletRequest request,@RequestParam Integer[] ids){
		if(!isAllowAccess()){
			return WebResult.NeedVerifyPassword();
		}else{
			return logService.executeDelete(ids,this.getCurrentUser());
		}
	}


}
