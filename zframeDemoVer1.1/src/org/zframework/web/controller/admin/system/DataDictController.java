package org.zframework.web.controller.admin.system;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.DateUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.DataDict;
import org.zframework.web.entity.system.DataDictType;
import org.zframework.web.service.admin.system.DataDictService;
import org.zframework.web.service.admin.system.DataDictTypeService;
import org.zframework.web.service.admin.system.LogService;

@Controller
@RequestMapping("/admin/datadict")
public class DataDictController extends BaseController<DataDict>{
	@Autowired
	private DataDictService ddService;
	@Autowired
	private DataDictTypeService ddtService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(method=RequestMethod.GET)
	public String index(Model model){
		if(ddtService.count() == 0){
			model.addAttribute("type","-1");
		}else{
			PageBean pageBean = new PageBean();
			pageBean.setRows(1);
			model.addAttribute("type",ddtService.listByPage(pageBean).get(0).getId());
		}
		return "admin/system/datadict/index";
	}
	
	@RequestMapping(value="/list",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(int type,PageBean pageBean,String name,String value){
		Map<String,Object> mapResult = new HashMap<String, Object>();
		if(type == -1){
			mapResult.put("rows", new String[]{});
			mapResult.put("total", 0);
		}else{
			pageBean.addCriterion(Restrictions.eq("dataDictType.id", type));
			return list(pageBean, name, value, ddService);
		}
		return mapResult;
	}
	
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String add(){
		return "admin/system/datadict/add";
	}

	@RequestMapping(value="/doAdd",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doAdd(String name,String dataType,String descript,String data){
		//判断字典数据名称是否已经存在，如存在则追加数据，否则则新建后新增数据
		DataDictType ddt = null;
		if(RegexUtil.isInteger(name)){//判断是否是ID
			ddt = ddtService.get_NoLazy(Restrictions.or(Restrictions.idEq(Integer.parseInt(name)),Restrictions.eq("name", name)));
		}else{
			ddt = ddtService.get_NoLazy(Restrictions.eq("name", name));
		}
		if(ObjectUtil.isNull(ddt)){
			ddt = new DataDictType();
			ddt.setName(name);
			ddt.setDataType(dataType);
			//ddt.setDescript("created by: "+getCurrentUser().getLoginName()+",datetime:"+DateUtil.getDateTime(new Date()));
			ddt.setDescript(descript);
			ddtService.save(ddt);
		}else{
			ddt.setDataType(dataType);
			ddt.setDescript(descript);
		}
		if(dataType.equals("VALUE")){
			DataDict dd = new DataDict();
			dd.setDataDictType(ddt);
			dd.setValue(data);
			dd.setLocation(1);
			dd.setDescript("created by: "+getCurrentUser().getLoginName()+",datetime:"+DateUtil.getDateTime(new Date()));
			
			List<DataDict> lstDD = ddt.getDatadicts();
			lstDD.clear();
			lstDD.add(dd);
			
			ddt.setDatadicts(lstDD);
			ddtService.update(ddt);
			
			return WebResult.success();
			
		}else if(dataType.equals("LIST")){
			String[] vals = data.split(",");
			List<DataDict> lstDD = ddt.getDatadicts();
			if(!ddt.getDataType().equals("LIST"))
				lstDD.clear();
			for(int i=0;i<vals.length;i++){
				DataDict dd = new DataDict();
				dd.setDataDictType(ddt);
				dd.setValue(vals[i]);
				dd.setLocation(i+1);
				dd.setDescript("created by: "+getCurrentUser().getLoginName()+",datetime:"+DateUtil.getDateTime(new Date()));
				
				lstDD.add(dd);
			}
			ddt.setDatadicts(lstDD);
			
			ddtService.update(ddt);
			
			return WebResult.success();
		}else if(dataType.equals("MAP")){
			JSONArray jDatas = JSONArray.fromObject("["+data+"]");
			List<DataDict> lstDD = ddt.getDatadicts();
			if(!ddt.getDataType().equals("MAP"))
				lstDD.clear();
			for(int i=0;i<jDatas.size();i++){
				JSONObject jObj = jDatas.getJSONObject(i);
				DataDict dd = getDataDictByName(lstDD,jObj.getString("k"));
				if(!jObj.getString("v").equals(dd.getValue())){
					dd.setDataDictType(ddt);
					dd.setName(jObj.getString("k"));
					dd.setValue(jObj.getString("v"));
					dd.setLocation(lstDD.size()+1);
					dd.setDescript("created by: "+getCurrentUser().getLoginName()+",datetime:"+DateUtil.getDateTime(new Date()));
					
					lstDD.add(dd);
				}
			}
			ddt.setDatadicts(lstDD);
			
			ddtService.update(ddt);
			return WebResult.success();
		}
		return WebResult.error("保存出错!");
	}
	private DataDict getDataDictByName(List<DataDict> lstDD,String name){
		DataDict dd = new DataDict();
		for(DataDict d : lstDD){
			if(name.equals(d.getName())){
				dd = d;
				break;
			}
		}
		return dd;
	}

	@RequestMapping(value="/edit/{id}",method={RequestMethod.GET})
	public String edit(Model model,@PathVariable Integer id){
		DataDict dd = ddService.getById_NoLazy(id);
		if(ObjectUtil.isNotNull(dd)){
			model.addAttribute("dd", dd);
			int totalCount = ddService.count(Restrictions.eq("dataDictType", dd.getDataDictType()));
			model.addAttribute("total", totalCount == 0?1:totalCount);
		}
		else//非法访问
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		return "admin/system/datadict/edit";
	}

	@RequestMapping(value="/doEdit",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doEdit(DataDict dd){
		DataDict eqDD = ddService.getById(dd.getId());
		if(ObjectUtil.isNotNull(eqDD)){
			eqDD.setName(dd.getName());
			eqDD.setValue(dd.getValue());
			if(dd.getLocation()!=eqDD.getLocation()){
				ddService.executeHQL("update DataDict set location="+eqDD.getLocation()+" where location="+dd.getLocation());
				eqDD.setLocation(dd.getLocation());
			}
			ddService.update(eqDD);
			return WebResult.success();
		}else{
			return WebResult.error("非法访问!");
		}
	}

	@RequestMapping(value="/doDelete",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject doDelete(Integer[] ids){
		try {
			ddService.delete(ids);
			logService.recordInfo("数据字典", "删除成功!", getCurrentUser().getLoginName(), getRequestAddr());
			return WebResult.success();
		} catch (Exception e) {
			logService.recordError("数据字典", "删除失败!"+e.getMessage(), getCurrentUser().getLoginName(), getRequestAddr());
			return WebResult.error("删除失败!"+e.getMessage());
		}
	}
	@RequestMapping(value="/sync",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject sync(){
		try {
			ApplicationCommon.DATADICT.sync();
			return WebResult.success();
		} catch (Exception e) {
			return WebResult.error("同步失败："+e.getMessage());
		}
		
	}
}
