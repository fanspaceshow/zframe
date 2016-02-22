package org.zframework.web.controller.admin.system;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.SafeConfig;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.SafeConfigService;

@Controller
@RequestMapping("/admin/safe")
public class SafeController extends BaseController<SafeConfig>{
	@Autowired
	private SafeConfigService scService;
	@Autowired
	private LogService logService;
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		//获取IP策略
		String safeIpConfig = scService.getSaveConfigValue(getApplicationCommon("SafeIpConfig"));
		if(ObjectUtil.isNull(safeIpConfig))
			safeIpConfig = "Deny";
		model.addAttribute("safeIpConfig", safeIpConfig);
		//获取IP安全规则开关
		String IpRoleState = scService.getSaveConfigValue(getApplicationCommon("IpRoleState"));
		if(ObjectUtil.isNull(IpRoleState))
			safeIpConfig = "true";
		model.addAttribute("IpRoleState", IpRoleState);
		
		logService.recordInfo("查询安全管理","成功",getCurrentUser().getLoginName() , getRequestAddr());
		return "admin/system/safe/index";
	}
	@RequestMapping(value="/changeSafeIpType/{type}",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject changeSafeIpType(@PathVariable int type){
		return scService.executeChangeSafeIpType(type);
	}
	@RequestMapping(value="/openOrCloseIpRole/{state}",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject openOrCloseIpRole(@PathVariable boolean state){
		return scService.executeOpenOrCloseIpRole(state);
	}
	
}
