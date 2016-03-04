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
import org.zframework.web.entity.system.Projecttype;
import org.zframework.web.service.admin.system.ProjecttypeService;
@Controller
@RequestMapping("/admin/project")
public class ProjecttypeController extends BaseController<Projecttype>{
	@Autowired
	private ProjecttypeService projecttypeService;
	Log log  = LogFactory.getLog("ProjecttypeController");
	@RequestMapping(value="/showtypelist",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONArray showtypelist(){
		JSONArray json=new JSONArray();
		List<Projecttype> list=projecttypeService.getprojecttype();
		for(Projecttype pro:list){
			JSONObject jNode = new JSONObject();
			jNode.element("id", pro.getId());
			jNode.element("text", pro.getProjecttype());
			json.add(jNode);
		}
		log.error("json"+json);
		log.error("projecttype successed");
		return json;
	}
}
