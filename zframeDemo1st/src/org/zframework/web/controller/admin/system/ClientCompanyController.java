package org.zframework.web.controller.admin.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.orm.query.PageBean;
import org.zframework.web.entity.system.Client;
import org.zframework.web.entity.system.Project;
import org.zframework.web.service.admin.system.ClientCompanyService;
import org.zframework.web.service.admin.system.LogService;

@Controller
@RequestMapping("/admin/clientcompany")
public class ClientCompanyController {
	@Autowired
	private ClientCompanyService clientService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		return "admin/system/clientcompany/index";
  }
  
	/**
	 * 读取数据，在页面显示数据
	 * @param pageBean
	 * @param name
	 * @param value
	 * @return
	 */
	@RequestMapping(value="/clientList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> proList(PageBean pageBean,String name,String value){
		Map<String,Object> dataMap = new HashMap<String, Object>();
		List<Client> clientList = clientService.getproList(pageBean, name, value);
		dataMap.put("rows", clientList);
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
		return "admin/system/clientcompany/add";
	}
}


