package org.zframework.web.controller.admin.system;

import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.zframework.app.fschart.FSCharts_3DColumn;
import org.zframework.core.web.support.WebContextHelper;
import org.zframework.web.service.admin.system.ChartService;

@Controller
@RequestMapping("/admin/chart")
public class ChartController {
	@Autowired
	ChartService chartService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		Map<String,String> map = chartService.getOperaChart();
		FSCharts_3DColumn col = new FSCharts_3DColumn();
		col.setFormatNumberScale(0);
		String str = col.senior_genderCode(map, "功能模块访问量分析", "功能模块", "访问次数", 500, 800, WebContextHelper.getSession());
		model.addAttribute("str", str);
		
		JSONArray jCatalogs = new JSONArray();
		JSONArray jDatas = new JSONArray();
		
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			String catalog = iter.next();
			jCatalogs.add(catalog);
			jDatas.add(Integer.parseInt(map.get(catalog)));
		}
		model.addAttribute("catalogs",jCatalogs.toString());
		model.addAttribute("datas",jDatas.toString());
		return "admin/system/chart/index";
	}
}
