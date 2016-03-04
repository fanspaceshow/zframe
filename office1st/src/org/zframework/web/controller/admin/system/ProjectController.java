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
import org.zframework.web.entity.system.Project;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.ProjectService;


@Controller
@RequestMapping("/admin/project")
public class ProjectController extends BaseController<Project> {
	@Autowired
	private ProjectService projectService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		return "admin/system/project/index";
	}
	/***
	 * 列表显示数据
	 * @param PageBean
	 * @param name
	 * @param value
	 * */
	@RequestMapping(value="/proList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> proList(PageBean pageBean,String name,String value){
		Map<String,Object> proMap = new HashMap<String, Object>();
		List<Project> proList = projectService.getproList(pageBean, name, value);
		proMap.put("rows", proList);
		proMap.put("total", pageBean.getTotalCount());
		return proMap;
	}

	/**
	 * 转向增加页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(){
		return "admin/system/project/add";
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
	public JSONObject doAdd(HttpServletRequest request,@Valid @ModelAttribute("pro")Project pro,BindingResult result){
			
		if(result.hasErrors()){
			
			logService.recordInfo("增加数据字典","失败（未按要求填写表单）", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.error("请按要求填写表单!");
		}else{
			return projectService.executeAdd(request, pro,getCurrentUser());
		}
	
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
			return projectService.executeDelete(request, ids, jResult,this.getCurrentUser());
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
		Project pro = projectService.getPro(id);
		if(!ObjectUtil.isNull(pro)){
			model.addAttribute("pro",pro);
			return "admin/system/project/edit";
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
	public JSONObject doEdit(HttpServletRequest request,@Valid @ModelAttribute("pro")Project pro,BindingResult result){
		
		JSONObject jResult = new JSONObject();
		if(result.hasErrors()){
			jResult = WebResult.error("请按要求填写表单");
			logService.recordInfo("编辑数据字典","失败（未按要求填写表单）", getCurrentUser().getLoginName(), request.getRemoteAddr());
		}else{
			return projectService.executeEdit(request, pro,this.getCurrentUser());
		}
		return jResult;
	}
//	/***
//	 * 删除机构
//	 * @param request
//	 * @param comm 
//	 * @param result
//	 * @return
//	 * */
//	@RequestMapping(value="/delete/{id}",method={RequestMethod.POST})
//	@ResponseBody
//	public JSONObject getById(@PathVariable Integer id){
//		JSONObject jResult = new JSONObject();
//		Project pro = projectService.getPro(id);
//			
//		jResult.element("id",true);
//				 
//		return jResult;
//	}
	
}
