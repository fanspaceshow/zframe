package org.zframework.web.controller.admin.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Common;


import org.zframework.web.service.admin.system.CommonService;
import org.zframework.web.service.admin.system.LogService;

/**
 *@author zengchao
 *@time 2012-12-20 下午1:44:10
 */
@Controller
@RequestMapping("/admin/common")
public class CommonController extends BaseController<Common> {
	@Autowired
	private CommonService commonService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		return "admin/system/comm/index";
	}
	/***
	 * 列表显示数据字典
	 * @param PageBean
	 * @param name
	 * @param value
	 * */
	@RequestMapping(value="/commList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> commList(PageBean pageBean,String name,String value){
		Map<String,Object> commMap = new HashMap<String, Object>();
		List<Common> commList = commonService.getCommonList(pageBean, name, value);
		commMap.put("rows", commList);
		commMap.put("total", pageBean.getTotalCount());
		return commMap;
	}

	/**
	 * 转向增加页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(){
		return "admin/system/comm/add";
	}
	/**
	 * 增加数据字典
	 * @param request 用于记录日志
	 * @param comm 数据字典对象
	 * @param result 
	 * @return
	 */
	@RequestMapping(value="/doAdd",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(HttpServletRequest request,@Valid @ModelAttribute("comm")Common comm,BindingResult result){
		JSONObject jResult = new JSONObject();
		if(result.hasErrors()){
			jResult = WebResult.error("请按要求填写表单");
			logService.recordInfo("增加数据字典","失败（未按要求填写表单）", getCurrentUser().getLoginName(), request.getRemoteAddr());
		}else{
			return commonService.executeAdd(request, comm,getCurrentUser());
		}
		return jResult;
	}

	/***
	 * 删除机构
	 * @param request
	 * @param comm 
	 * @param result
	 * @return
	 * */
	@RequestMapping(value="/doDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpServletRequest request,@RequestParam Integer[] ids){
		JSONObject jResult = new JSONObject();
		if(!isAllowAccess()){
			jResult = WebResult.NeedVerifyPassword();
		}else{
			return commonService.executeDelete(request, ids, jResult,this.getCurrentUser());
		}
		return jResult;
	}

	/**
	 * 转向编辑页面
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/{id}",method={RequestMethod.GET})
	public String toEdit(Model model,@PathVariable Integer id){
		Common common = commonService.getCommon(id);
		if(!ObjectUtil.isNull(common)){
			model.addAttribute("common",common);
			return "admin/system/comm/edit";
		}else{
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
		
	}

	/***
	 * 确认编辑字典
	 * @param request
	 * @param unit 
	 * @param result
	 * @return
	 * */
	@RequestMapping(value="/doEdit",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(HttpServletRequest request,@Valid @ModelAttribute("comm")Common comm,BindingResult result){
		JSONObject jResult = new JSONObject();
		if(result.hasErrors()){
			jResult = WebResult.error("请按要求填写表单");
			logService.recordInfo("编辑数据字典","失败（未按要求填写表单）", getCurrentUser().getLoginName(), request.getRemoteAddr());
		}else{
			return commonService.executeEdit(request, comm,this.getCurrentUser());
		}
		return jResult;
	}
	/***
	 * 删除机构
	 * @param request
	 * @param comm 
	 * @param result
	 * @return
	 * */
	@RequestMapping(value="/getTypeById/{id}",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject getTypeById(@PathVariable Integer id){
		JSONObject jResult = new JSONObject();
			Common common = commonService.getCommon(id);
			if(common.getType()==1){
				jResult.element("isType", true);
			}
	 
		return jResult;
	}
	
}
