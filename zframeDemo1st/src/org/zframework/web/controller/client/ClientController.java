package org.zframework.web.controller.client;

import java.io.UnsupportedEncodingException;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.util.ObjectUtil;
/**
 * 针对移动终端的入口类
 * @author zengchao
 *
 */
@Controller
@RequestMapping("/client")
public class ClientController {
	@RequestMapping(value="/login",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public String login(String userName, String pass){
		if(ObjectUtil.isNull(userName) || ObjectUtil.isNull(pass) || userName.length() == 0 || pass.length() == 0){
			return "Error!!";
		}
		try {
			userName =  new String(userName.getBytes("iso8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(userName + ":" + pass);
		return "Success!";
	}
	
	@RequestMapping(value="/json",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONObject json(){
		JSONObject o = new JSONObject();
		
		o.element("id", 1);
		o.element("name", "张三");
		o.element("age", 22);
		return o;
	}
}
