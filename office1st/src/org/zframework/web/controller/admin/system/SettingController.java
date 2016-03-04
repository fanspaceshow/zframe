package org.zframework.web.controller.admin.system;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/admin/setting")
public class SettingController {
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		return "admin/system/setting/index";
	}
}
