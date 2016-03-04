package org.zframework.web.controller.admin.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import net.sf.json.JSONObject;

import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Button;
import org.zframework.web.service.admin.system.ButtonService;
import org.zframework.web.service.admin.system.LogService;
@Controller
@RequestMapping("/admin/button")
public class ButtonController extends BaseController<Button>{
	@Autowired
	private ButtonService btnService;
	@Autowired
	private LogService logService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		logService.recordInfo("查询操作按钮","成功",getCurrentUser().getLoginName() , getRequestAddr());
		return "admin/system/button/index";
	}
	/**
	 * 按钮列表
	 * @param pageBean
	 * @param name
	 * @param value
	 * @return
	 */
	@RequestMapping(value="/btnList",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> btnList(PageBean pageBean,String name,String value){
		return list(pageBean, name, value, btnService);
	}
	/**
	 * 按钮列表
	 * @param pageBean
	 * @param name
	 * @param value
	 * @return
	 */
	@RequestMapping(value="/btnListForResource",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> btnListForResource(PageBean pageBean,String name,String value,boolean isEq,Integer[] ids){
		Map<String,Object> btnMap = new HashMap<String, Object>();
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value))
					pageBean.addCriterion(Restrictions.eq(name, Integer.parseInt(value)));
			}else{
				pageBean.addCriterion(Restrictions.like(name, "%"+value+"%"));
			}
		}
		pageBean.addCriterion(Restrictions.eq("enabled", 0));
		if(isEq){//如果是获取已经存在的按钮
			if(ObjectUtil.isNotEmpty(ids)){
				pageBean.addCriterion(Restrictions.in("id", ids));
			}else{
				pageBean.addCriterion(Restrictions.idEq(-1));
			}
		}else{
			if(ObjectUtil.isNotEmpty(ids))
				pageBean.addCriterion(Restrictions.not(Restrictions.in("id", ids)));
		}
		List<Button> btnList = btnService.list(pageBean.getCriterionsArray());
		//ReflectUtil.removeLazyProperty(btnList);
		btnMap.put("rows", btnList);
		btnMap.put("total", pageBean.getTotalCount());
		return btnMap;
	}
	/**
	 * 转向新增按钮界面
	 * @return
	 */
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(){
		return "admin/system/button/add";
	}
	/**
	 * 执行新增按钮
	 * @return
	 */
	@RequestMapping(value="/doAdd",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(HttpServletRequest request,@Valid @ModelAttribute("button")Button button,BindingResult result){
		JSONObject jResult = new JSONObject();
		if(result.hasErrors()){
			jResult = WebResult.error("请按要求填写表单!");
			//记录日志
			logService.recordInfo("新增按钮","失败(非法提交表单!)",getCurrentUser().getLoginName() , request.getRemoteAddr());
		}else{
			jResult = btnService.executeAdd(request, button,getCurrentUser());
		}
		return jResult;
	}
	/**
	 * 转向编辑按钮界面
	 * @return
	 */
	@RequestMapping(value="/edit/{id}",method={RequestMethod.GET})
	public String toEdit(Model model,@PathVariable Integer id){
		Button btn = btnService.getById(id);
		if(ObjectUtil.isNotNull(btn)){
			model.addAttribute("btn", btn);
			return "admin/system/button/edit";
		}else{
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
	}
	/**
	 * 执行新增按钮
	 * @return
	 */
	@RequestMapping(value="/doEdit",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(HttpServletRequest request,@Valid @ModelAttribute("button")Button button,BindingResult result){
		if(result.hasErrors()){
			logService.recordInfo("编辑按钮","失败(未按要求填写表单)", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.error("请按要求填写表单!");
		}else{
			return btnService.executeEdit(request, button, result,getCurrentUser());
		}
	}
	/**
	 * 执行删除
	 * @param request
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/doDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(@RequestParam Integer[] ids){
		return btnService.executeDelete(ids);
	}
	/**
	 * 锁定或者解锁按钮
	 * @param ids
	 * @param 0 锁定，1解锁
	 * @return
	 */
	@RequestMapping(value="/lock",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject lockOrUnLockUser(Integer[] ids,int type){
		JSONObject jResult = new JSONObject();
		if(ObjectUtil.isNotEmpty(ids))
			jResult = btnService.executeLockOrUnLockUser(ids, type);
		else{
			String resultTag = "isLocked";
			if(type == 1){
				resultTag = "isUnLocked";
			}
			jResult.element(resultTag, false);
			jResult.element("error", "非法操作!");
		}
		return jResult;
	}
}
