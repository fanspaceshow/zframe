package org.zframework.web.controller.admin.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
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
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.JSONUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Button;
import org.zframework.web.entity.system.Resource;
import org.zframework.web.service.admin.system.ButtonService;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.ResourceService;
@Controller
@RequestMapping("/admin/res")
public class ResourceController extends BaseController<Resource>{
	@Autowired
	private ResourceService resService;
	@Autowired
	private ButtonService btnService;
	@Autowired
	private LogService logService;
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		logService.recordInfo("查询资源","成功",getCurrentUser().getLoginName() , getRequestAddr());
		return "admin/system/res/index";
	}
	@RequestMapping(value="/resList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> resList(String name,String value){
		Map<String,Object> resMap = new HashMap<String, Object>();
		List<Criterion> criterionList = new ArrayList<Criterion>();
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value))
					criterionList.add(Restrictions.idEq(Integer.parseInt(value)));
			}else{
				criterionList.add(Restrictions.like(name, "%"+value+"%"));
			}
		}
		List<Resource> resList = resService.list_noLazyButtons(criterionList.toArray(new Criterion[]{}),Order.asc("location"),Order.asc("parentId"));
		List<JSONObject> jResList = new ArrayList<JSONObject>();
		if(resList!=null){
			for(Resource res : resList){
				JSONObject jRes = JSONUtil.toJsonObject(res,"roles");
				if(StringUtil.isEmpty(value)){//如果不存在查询条件，则设置父节点
					jRes.element("_parentId", res.getParentId()==0?"":res.getParentId());
				}else{//如果存在查询条件，那么有父节点的还按照树形来显示
					if(hasPropertyValueInArray(jResList, "id", res.getParentId())){
						jRes.element("_parentId", res.getParentId()==0?"":res.getParentId());
					}
				}
				jRes.element("iconCls", res.getIcon());
				jResList.add(jRes);
			}
		}
		resMap.put("rows", jResList);
		resMap.put("total", jResList.size());
		return resMap;
	}
	/**
	 * 获取子菜单
	 * @param 父节点
	 * @return
	 */
	@RequestMapping(value="/child/{parentId}",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> childList(@PathVariable String parentId){
		Map<String,Object> childResMap = new HashMap<String, Object>();
		return childResMap;
	}
	/**
	 * 转向增加页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(Model model){
		return "admin/system/res/add";
	}
	/**
	 * 增加资源
	 * @param request 用于记录日志
	 * @param res 资源对象
	 * @param result 
	 * @return
	 */
	@RequestMapping(value="/doAdd",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(HttpServletRequest request,@Valid @ModelAttribute("res")Resource res,BindingResult result){
		if(result.hasErrors()){
			//记录日志
			logService.recordInfo("新增资源","失败",getCurrentUser().getLoginName() , request.getRemoteAddr());
			return WebResult.error("请按要求填写表单!");
		}else{	
			return resService.executeAdd(request, res,getCurrentUser());
		}
	}
	/**
	 * 获取子资源
	 * @param 父节点ID 
	 * 如果不存在父节点Id或者父节点Id为0，则返回的为顶级资源
	 * 返回中包括自定义资源 无上级资源
	 * @return
	 */
	@RequestMapping(value="/resTree")
	@ResponseBody
	public JSONArray resTree(@RequestParam(value="id",required=false) Integer parentId){
		JSONArray jResTree = new JSONArray();
		if(ObjectUtil.isNull(parentId)){
			parentId = 0;
			JSONObject jNode = new JSONObject();
			jNode.element("id", 0);
			jNode.element("text", "无上级资源");
			jNode.element("iconCls", "icon-ok");
			jResTree.add(jNode);
		}
		List<Resource> list = resService.list(new Criterion[]{Restrictions.eq("parentId", parentId)},Order.asc("location"),Order.asc("parentId"));
		if(ObjectUtil.isNotNull(list)){
			for(Resource res : list){
				JSONObject jNode = new JSONObject();
				jNode.element("id", res.getId());
				jNode.element("text", res.getName());
				jNode.element("iconCls", res.getIcon());
				jNode.element("url", res.getUrl());
				if(parentId == 0)
					jNode.element("state", "closed");
				jResTree.add(jNode);
			}
		}
		return jResTree;
	}
	/**
	 * 转向编辑页面
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/edit/{id}",method={RequestMethod.GET})
	public String toEdit(Model model,@PathVariable Integer id){
		//获取资源对象
		Resource res = resService.getById(id);
		//如果存在，则加入此对象
		if(ObjectUtil.isNotNull(res)){
			model.addAttribute("res", res);
			if(res.getParentId()==0){
				model.addAttribute("parentResName","无上级资源");
			}else{
				Resource parentRes = resService.getById(res.getParentId());
				model.addAttribute("parentResName",parentRes.getName());
			}
			return "admin/system/res/edit";
		}else{
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
	}
	/**
	 * 编辑资源
	 * @param request
	 * @param res
	 * @param result
	 * @return
	 */
	@RequestMapping(value="/doEdit",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(HttpServletRequest request,@Valid @ModelAttribute("res")Resource res,BindingResult result){
		if(result.hasErrors()){
			logService.recordInfo("编辑资源","失败(未按要求填写表单)", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.error("请按要求填写表单!");
		}else{
			return resService.executeEdit(request, res,getCurrentUser());
		}
	}
	
	/**
	 * 删除资源
	 * @param request
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/doDelete",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpServletRequest request,@RequestParam Integer[] ids){
		if(!isAllowAccess()){
			return WebResult.NeedVerifyPassword();
		}else{
			//删除选中资源
			resService.deleteRes(ids);
			//如果选中资源是父级资源，则删除其子资源
			logService.recordInfo("删除资源","成功", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.success();
		}
	}
	/**
	 * 转向资源排序
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/sort",method={RequestMethod.GET})
	public String toSort(Model model){
		model.addAttribute("childRes", resService.list(Restrictions.eq("parentId", 0),Order.asc("location")));
		return "admin/system/res/sort";
	}
	@RequestMapping(value="/childRes",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public JSONArray childRes(int parentId){
		JSONArray childRes = new JSONArray();
		List<Resource> list = resService.list(new Criterion[]{Restrictions.eq("parentId", parentId)},Order.asc("location"),Order.asc("parentId"));
		if(ObjectUtil.isNotNull(list)){
			for(Resource res : list){
				JSONObject jNode = new JSONObject();
				jNode.element("id", res.getId());
				jNode.element("name", res.getName());
				jNode.element("icon", res.getIcon());
				childRes.add(jNode);
			}
		}
		return childRes;
	}
	/**
	 * 资源排序
	 * @return
	 */
	@RequestMapping(value="/doSort",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doSort(HttpServletRequest request,Integer parentId,Integer[] ids){
		try {
			boolean result = resService.executeSort(parentId, ids);
			if(result){
				logService.recordInfo("资源排序","成功", getCurrentUser().getLoginName(), request.getRemoteAddr());
				return WebResult.success();
			}else{
				logService.recordInfo("资源排序","失败", getCurrentUser().getLoginName(), request.getRemoteAddr());
				return WebResult.error("排序失败");
			}
		} catch (Exception e) {
			logService.recordInfo("资源排序","失败(数据库操作异常)", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.error("排序失败");
		}
	}
	/**
	 * 转向分配按钮页面
	 * @param model
	 * @param id
	 * @return
	 */
	@RequestMapping(value="/assignButton/{id}",method={RequestMethod.GET})
	public String assignButton(Model model,@PathVariable Integer id){
		Resource res = resService.getById_NoLazyButtons(id);
		List<Button> btns = res.getButtons();
		Integer[] ids = new Integer[btns.size()];
		for(int i=0;i<btns.size();i++){
			ids[i] = btns.get(i).getId();
		}
		model.addAttribute("btnIds",StringUtil.toString(ids));
		model.addAttribute("resId", id);
		return "admin/system/res/assignButton";
	}
	@RequestMapping(value="/doAssignButton",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAssignButton(HttpServletRequest request,Integer resId,Integer[] btnIds){
		if(ObjectUtil.isNotEmpty(btnIds)){
			List<Button> selectedBtns = btnService.getByIds(btnIds);
			Resource res = resService.getById(resId);
			res.setButtons(selectedBtns);
			resService.update(res);
			logService.recordInfo("分配按钮","成功", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.success();
		}else{
			Resource res = resService.getById(resId);
			res.setButtons(null);
			resService.update(res);
			logService.recordInfo("分配按钮","成功(删除所有按钮权限)", getCurrentUser().getLoginName(), request.getRemoteAddr());
			return WebResult.success();
		}
	}
	/**
	 * 获取资源下所有的按钮
	 * 提供给角色管理分配权限模块
	 * @param  资源ID
	 * @return
	 */
	@RequestMapping(value="/resourceButtonsForRole",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> resourceButtonsForRole(@RequestParam(value="resId",required=false) Integer resId){
		Map<String,Object> result = new HashMap<String, Object>();
		List<Button> resBtns = new ArrayList<Button>(); 
		if(ObjectUtil.isNotNull(resId)){
			Resource res = resService.getById_NoLazyButtons(resId);
			if(ObjectUtil.isNotNull(res)){
				resBtns = res.getButtons();
			}
		}
		//转换成Json对象集合，移除延迟属性
		result.put("rows", JSONUtil.toJsonObjectListNoLazy(resBtns));
		result.put("total", resBtns.size());
		return result;
	}
	/**
	 * 选择资源图标
	 * @return
	 */
	@RequestMapping(value="/selIcon")
	public String selIcon(Model model,HttpServletRequest request){
		model.addAttribute("list",ApplicationCommon.ICONCLS_LIST);
		return "admin/system/res/selIcon";
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
			jResult = resService.executeLockOrUnLockUser(ids, type);
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
