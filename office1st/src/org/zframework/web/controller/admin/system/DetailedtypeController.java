package org.zframework.web.controller.admin.system;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Detailedtype;
import org.zframework.web.service.admin.system.DetailedtypeService;

@Controller
@RequestMapping("/admin/project")
public class DetailedtypeController extends BaseController<Detailedtype>{
	@Autowired
	private DetailedtypeService detailedtypeService;
	@RequestMapping(value="/showlist",method={RequestMethod.POST})
	@ResponseBody
	public JSONArray shoulist(HttpServletRequest request,@RequestParam String projecttype){
		JSONArray json=new JSONArray();
		 List<Detailedtype> list=detailedtypeService.getlist(projecttype);
		 for(Detailedtype deta:list){
			 JSONObject jNode = new JSONObject();
			 jNode.element("id", deta.getId());
			 jNode.element("text", deta.getDetailedtype());
			 json.add(jNode);
		 }
		return json;
	}

}
