package org.zframework.web.controller.admin.system;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.CompanyType;
import org.zframework.web.service.admin.system.CompanyTypeService;

@Controller
@RequestMapping("/admin/clientcompany")
public class CompanyTypeController extends BaseController<CompanyType>{
	@Autowired
	private CompanyTypeService companytypeservice;
	Log log  = LogFactory.getLog("CompanyTypeController");
	
	@RequestMapping(value="/showtypelist",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONArray showtypelist(){
		log.error("into CompanyTypeController showtypelist");
		JSONArray json=new JSONArray();
		List<CompanyType> list=companytypeservice.getCompanyType();
		for(CompanyType com:list){
			JSONObject jNode = new JSONObject();
			jNode.element("id", com.getId());
			jNode.element("text", com.getCompany());
			json.add(jNode);
		}
		return json;
	}
}
