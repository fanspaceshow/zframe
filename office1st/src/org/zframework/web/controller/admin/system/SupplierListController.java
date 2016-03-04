package org.zframework.web.controller.admin.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.zframework.web.entity.system.Measureunits;
import org.zframework.web.entity.system.SupplierList;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.SupplierListService;

@Controller
@RequestMapping("/user/suppliercontroller")
public class SupplierListController extends BaseController<SupplierList>{
	
	@Autowired
	private SupplierListService supplierlistservice;
	@Autowired
	private LogService logService;
	
	Log log  = LogFactory.getLog("SupplierListController");
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		return "admin/system/supplierlist/index";
  }
	
	/**
	 * 读取数据，在页面显示数据
	 * @param pageBean
	 * @param name
	 * @param value
	 * @return
	 */
	@RequestMapping(value="/suplierList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> proList(PageBean pageBean,String name,String value){
		Map<String,Object> dataMap = new HashMap<String, Object>();
		List<SupplierList> supplierList = supplierlistservice.getproList(pageBean, name, value);
		dataMap.put("rows", supplierList);
		dataMap.put("total", pageBean.getTotalCount());
		return dataMap;
	}
	
	
	/**
	 * 转向增加页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(){
		return "admin/system/supplierlist/add";
	}
	
	/**
	 * 保存数据
	 * @param request 用于记录日志
	 * @param comm 数据字典对象
	 * @param result 
	 * @return
	 */
	@RequestMapping(value="/doAdd",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(HttpServletRequest request,@Valid @ModelAttribute("supplier")SupplierList supplier,BindingResult result){
		log.error("into supplier doAdd from log.debug!!!");
		if(result.hasErrors()){	
			log.error("hass errors,"+result.getFieldError());
			logService.recordInfo("增加数据字典","失败（未按要求填写表单）", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.error("请按要求填写表单!");
		}else{
			log.error("into server");
			return supplierlistservice.executeAdd(request, supplier,getCurrentUser());
		}	
	}
	
	
	
	/***
	 * 删除方法
	 * @param request
	 * @param comm 
	 * @param result
	 * @return
	 * */
	@RequestMapping(value="/doDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpServletRequest request,@RequestParam Integer[] ids){
		log.error("into supplier doDelete");
		JSONObject jResult = new JSONObject();
		if(!isAllowAccess()){
			jResult = WebResult.NeedVerifyPassword();
		}else{
			return supplierlistservice.executeDelete(request, ids, jResult,this.getCurrentUser());
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
		log.error("into supplier toEdit");
		SupplierList supplier = supplierlistservice.getPro(id);
		if(!ObjectUtil.isNull(supplier)){
			model.addAttribute("pro",supplier);
			return "admin/system/supplierlist/edit";
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
	public JSONObject doEdit(HttpServletRequest request,@Valid @ModelAttribute("supplier")SupplierList supplier,BindingResult result){
		log.error("into supplier doEdit");
		JSONObject jResult = new JSONObject();
		if(result.hasErrors()){
			log.error("haserrors: "+result.hasErrors());
			jResult = WebResult.error("请按要求填写表单");
			logService.recordInfo("编辑数据字典","失败（未按要求填写表单）", getCurrentUser().getLoginName(), request.getRemoteAddr());
		}else{
			log.error("into supplier executeEdit");
			return supplierlistservice.executeEdit(request, supplier,this.getCurrentUser());
		}
		return jResult;
	}
	
	
	@RequestMapping(value="/showsupplierlist",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONArray showtypelist(){
		JSONArray json=new JSONArray();
		List<SupplierList> list=supplierlistservice.getSupplierlist();
		for(SupplierList supplier:list){
			JSONObject jNode = new JSONObject();
			jNode.element("id", supplier.getId());
			jNode.element("text", supplier.getSuppliername());
			json.add(jNode);
		}
		return json;
	}
}
