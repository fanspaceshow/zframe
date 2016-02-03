package org.zframework.web.controller.admin.system;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.DataDictType;
import org.zframework.web.service.admin.system.DataDictTypeService;
import org.zframework.web.service.admin.system.LogService;

@Controller
@RequestMapping("/admin/datadicttype")
public class DataDictTypeController extends BaseController<DataDictType> {
	
	@Autowired
	private DataDictTypeService ddtService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(value="/list",method={RequestMethod.POST})
	@ResponseBody
	public List<JSONObject> list(){
		List<JSONObject> lstJson = new ArrayList<JSONObject>();
		List<DataDictType> lstDDT = ddtService.list();
		for(DataDictType ddt : lstDDT){
			JSONObject jDDT = new JSONObject();
			jDDT.element("id", ddt.getId());
			jDDT.element("text", ddt.getName());
			
			lstJson.add(jDDT);
		}
		logService.recordInfo("数据字典", "查询数据字典(list)", getCurrentUser().getLoginName(), getRequestAddr());
		return lstJson;
	}
	@RequestMapping(value="/tree",method={RequestMethod.POST})
	@ResponseBody
	public List<JSONObject> treeList(){
		List<JSONObject> lstJson = new ArrayList<JSONObject>();
		List<DataDictType> lstDDT = ddtService.list();
		JSONObject jValues = new JSONObject();
		jValues.element("id", "-1");
		jValues.element("text", "VALUE");
		jValues.element("iconCls", "icon-assets-book-open");
		JSONArray jVChilds = new JSONArray();
		JSONObject jList = new JSONObject();
		jList.element("id", "-1");
		jList.element("text", "LIST");
		jList.element("iconCls", "icon-assets-book-open");
		JSONArray jLSTChilds = new JSONArray();
		JSONObject jMap = new JSONObject();
		jMap.element("id", "-1");
		jMap.element("text", "MAP");
		jMap.element("iconCls", "icon-assets-book-open");
		JSONArray jMapChilds = new JSONArray();
		
		for(DataDictType ddt : lstDDT){
			JSONObject jDDT = new JSONObject();
			jDDT.element("id", ddt.getId());
			jDDT.element("text", ddt.getName());
			jDDT.element("iconCls", "icon-datadict");
			jDDT.element("desc", ddt.getDescript());
			if(ddt.getDataType().equals("VALUE")){
				jVChilds.add(jDDT);
			}else if(ddt.getDataType().equals("LIST")){
				jLSTChilds.add(jDDT);
			}else if(ddt.getDataType().equals("MAP")){
				jMapChilds.add(jDDT);
			}
				
		}
		if(jVChilds.size()>0){
			jVChilds.getJSONObject(0).element("checked", true);
			jValues.element("state", "open");
			
		}else if(jLSTChilds.size()>0){
			jLSTChilds.getJSONObject(0).element("checked", true);
			jList.element("state", "open");
			
		}else if(jMapChilds.size()>0){
			jMapChilds.getJSONObject(0).element("checked", true);
			jMap.element("state", "open");
			
		}
		
		jValues.element("children", jVChilds);
		lstJson.add(jValues);
		
		jList.element("children", jLSTChilds);
		lstJson.add(jList);
		
		jMap.element("children", jMapChilds);
		lstJson.add(jMap);
		
		logService.recordInfo("数据字典", "查询数据字典(tree)", getCurrentUser().getLoginName(), getRequestAddr());
		return lstJson;
	}
	@RequestMapping(value="/delete/{id}",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject del(@PathVariable Integer id){
		ddtService.delete(id);
		logService.recordInfo("数据字典", "删除数据字典", getCurrentUser().getLoginName(), getRequestAddr());
		return WebResult.success();
	}
}
