package org.zframework.web.controller.admin.system;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import net.sf.json.JSONObject;

import org.hibernate.criterion.Criterion;
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
import org.zframework.core.util.DateUtil;
import org.zframework.core.util.DecUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.PageBean;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Role;
import org.zframework.web.entity.system.Unit;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.RoleService;
import org.zframework.web.service.admin.system.UnitService;
import org.zframework.web.service.admin.system.UserService;

@Controller
@RequestMapping(value="/admin/user")
public class UserController extends BaseController<User>{
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private UnitService unitService;
	@Autowired
	private LogService logService;
	/**
	 * 用户管理首页
	 * @param model
	 * @return
	 */
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		logService.recordInfo("查询用户","成功",getCurrentUser().getLoginName() , getRequestAddr());
		return "admin/system/user/index";
	}
	@RequestMapping(value="/userList",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> userList(PageBean pageBean,String[] name,String[] value){
		Map<String,Object> userMap = new HashMap<String, Object>();
		if(ObjectUtil.isNotEmpty(name) && ObjectUtil.isNotEmpty(value)){
			for(int i=0;i<value.length;i++){
				String n = name[i];
				String v = value[i];
				if("id".equals(n)){
					if(RegexUtil.isInteger(v))
						pageBean.addCriterion(Restrictions.eq(n, Integer.parseInt(v)));
				}else{
					pageBean.addCriterion(Restrictions.like(n, "%"+v+"%"));
				}
			}
		}
		
		//判断是否是超级管理员，如果是超级管理员，能看到所有用户
		//如果不是超级管理员则只能看到本单位下(包括本单位)的用户
		if(!getCurrentUser().getLoginName().equals(ApplicationCommon.SYSTEM_ADMIN)){
			pageBean.addCriterion(Restrictions.not(Restrictions.eq("loginName", ApplicationCommon.SYSTEM_ADMIN)));
			List<Unit> curentUserChildUnits = unitService.getAllChildUnitListHasSelf_NoLazyUser(getCurrentUser().getUnits().get(0).getId());
			Integer[] ids = getUserIdsInUnitList(curentUserChildUnits);
			if(ids.length>0)
				pageBean.addCriterion(Restrictions.in("id", ids));
		}
		List<User> userList = userService.listByPage(pageBean);
		//ReflectUtil.removeLazyProperty(userList);
		userMap.put("rows", userList);
		userMap.put("total", pageBean.getTotalCount());
		return userMap;
	}
	@RequestMapping(value="/userListForNotice",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> userListForNotice(PageBean pageBean,String[] name,String[] value){
		Map<String,Object> userMap = new HashMap<String, Object>();
		if(ObjectUtil.isNotEmpty(name) && ObjectUtil.isNotEmpty(value)){
			for(int i=0;i<value.length;i++){
				String n = name[i];
				String v = value[i];
				if("id".equals(n)){
					if(RegexUtil.isInteger(v))
						pageBean.addCriterion(Restrictions.eq(n, Integer.parseInt(v)));
				}else{
					pageBean.addCriterion(Restrictions.like(n, "%"+v+"%"));
				}
			}
		}
		pageBean.addCriterion(Restrictions.not(Restrictions.eq("loginName", ApplicationCommon.SYSTEM_ADMIN)));

		List<User> userList = userService.list(pageBean.getCriterions().toArray(new Criterion[]{}));
		//ReflectUtil.removeLazyProperty(userList);
		userMap.put("rows", userList);
		userMap.put("total", pageBean.getTotalCount());
		return userMap;
	}
	/**
	 * 获取单位集合中所有的用户ID
	 * @param unitList
	 * @return
	 */
	private Integer[] getUserIdsInUnitList(List<Unit> unitList){
		List<Integer> ids = new ArrayList<Integer>();
		for(Unit unit : unitList){
			List<User> userList = unit.getUsers();
			for(User user : userList){
				if(!ids.contains(new Integer(user.getId()))){
					ids.add(user.getId());
				}
			}
		}
		return ids.toArray(new Integer[]{});
	}
	
	@RequestMapping(value="/add",method={RequestMethod.GET})
	public String toAdd(Model model){
		model.addAttribute("defaultUnit", getApplicationCommon("SystemUnit"));
		model.addAttribute("defaultRole", getApplicationCommon("OrdinaryRole"));
		return "admin/system/user/add";
	}
	@RequestMapping(value="/doAdd",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAdd(HttpSession session,HttpServletRequest request,@Valid @ModelAttribute("user")User user,BindingResult result){
		if(result.hasErrors()){
			//记录日志
			logService.recordInfo("新增用户","失败",getCurrentUser().getLoginName() , request.getRemoteAddr());
			return WebResult.error("请按要求填写表单!");
		}else{
			User eqUser = userService.getByLoginName(user.getLoginName());//根据登录名获取用户，
			//记录日志
			User currentUser = (User) session.getAttribute(ApplicationCommon.SESSION_ADMIN);
			if(ObjectUtil.isNull(eqUser)){
				//获取选择的角色
				String userRoles = request.getParameter("userRoles");
				List<Role> roleList = new ArrayList<Role>();
				//如果为空，则默认为普通角色
				if(StringUtil.isEmpty(userRoles.trim())){
					Role role = roleService.getByName(getApplicationCommon("OrdinaryRole"));
					if(ObjectUtil.isNotNull(role)){
						roleList.add(role);
					}else{
						return WebResult.error("普通角色已不存在!");
					}
				}else{
					roleList = roleService.list(Restrictions.in("id", StringUtil.toIntArray(userRoles, ",")));
				}
				user.setRoles(roleList);
				
				//获取选择的机构
				String userUnit = request.getParameter("userUnits");
				List<Unit> unitList = new ArrayList<Unit>();
				//如果为空，则默认为系统管理机构
				if(StringUtil.isEmpty(userUnit)){
					Unit unit = unitService.getByName(getApplicationCommon("SystemUnit"));
					if(ObjectUtil.isNotNull(unit)){
						unitList.add(unit);
					}else{
						return WebResult.error("系统管理机构已不存在!");
					}
				}else{
					unitList = unitService.list(Restrictions.in("id", StringUtil.toIntArray(userUnit, ",")));
				}
				user.setUnits(unitList);
				//将密码加密
				DecUtil des = new DecUtil();
				des.genKey(ApplicationCommon.DEC_KEY);// 生成密匙
				user.setPassWord(des.getEncString(user.getPassWord()));
				//设置创建时间
				user.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
				userService.save(user);//保存到数据库中
				logService.recordInfo("新增用户","成功",currentUser.getLoginName() , request.getRemoteAddr());
				return WebResult.success();
			}else{
				logService.recordInfo("新增用户","失败（登录名重复）",currentUser.getLoginName() , request.getRemoteAddr());
				return WebResult.error("此登录名已经存在!");
			}
		}
	}
	@RequestMapping(value="/doDelete",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDelete(HttpSession session,HttpServletRequest request,@RequestParam Integer[] ids){
		JSONObject jResult = new JSONObject();
		if(!isAllowAccess()){
			return WebResult.NeedVerifyPassword();
		}else{
			//记录日志
			User currentUser = (User) session.getAttribute(ApplicationCommon.SESSION_ADMIN);
			List<User> userList = userService.getByIds(ids);
			for(User user : userList){
				if(ObjectUtil.isNull(user)){
					jResult = WebResult.error("尝试删除不存在的用户");
					logService.recordInfo("删除用户","失败（尝试删除不存在的用户）",currentUser.getLoginName() , request.getRemoteAddr());
					break;
				}else if(user.getLoginName().equals(ApplicationCommon.SYSTEM_ADMIN)){
					jResult = WebResult.error("系统管理员用户不可删除");
					logService.recordInfo("删除用户","失败（拒绝删除系统管理员）",currentUser.getLoginName() , request.getRemoteAddr());
					break;
				}else{
					userService.delete(user);
					logService.recordInfo("删除用户","成功（用户ID:"+user.getId()+",用户登录名:"+user.getLoginName()+")",currentUser.getLoginName() , request.getRemoteAddr());
					return WebResult.success();
				}
			}
		}
		return jResult;
	}
	@RequestMapping(value="/edit/{id}",method={RequestMethod.GET})
	public String toEdit(Model model,@PathVariable Integer id){
		User user = userService.getById_NoLazy(id);
		StringBuffer ids = new StringBuffer();
		StringBuffer names = new  StringBuffer();
		//获取用户已选择的角色列表
		List<Role> roleList = user.getRoles();
		if(ObjectUtil.isNotNull(roleList) && ObjectUtil.isNotEmpty(roleList)){
			for(Role role : roleList){
				ids.append(","+role.getId());
				names.append(",["+role.getName()+"]");
			}
			model.addAttribute("roleids",ids.substring(1));
			model.addAttribute("rolenames",names.substring(1));
		}
		//获取用户已选择的机构列表
		List<Unit> unitList = user.getUnits();
		ids = new StringBuffer();
		names = new StringBuffer();
		if(ObjectUtil.isNotNull(unitList) && ObjectUtil.isNotEmpty(unitList)){
			for(Unit unit : unitList){
				ids.append(","+unit.getId());
				names.append(",["+unit.getName()+"]");
			}
			model.addAttribute("unitids",ids.substring(1));
			model.addAttribute("unitnames",names.substring(1));
		}
		model.addAttribute("user", user);
		model.addAttribute("defaultUnit", getApplicationCommon("SystemUnit"));
		model.addAttribute("defaultRole", getApplicationCommon("OrdinaryRole"));
		return "admin/system/user/edit";
	}
	@RequestMapping(value="/doEdit",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doEdit(HttpSession session,HttpServletRequest request,@Valid @ModelAttribute("user")User user,BindingResult result){
		if(result.hasErrors()){
			//记录日志
			logService.recordInfo("编辑用户","失败",getCurrentUser().getLoginName() , request.getRemoteAddr());
			return WebResult.error("请按要求填写表单!");
		}else{
			User updateUser = userService.getById(user.getId());
			if(ObjectUtil.isNotNull(updateUser)){
				//设置需要更新的字段
				updateUser.setRealName(user.getRealName());
				updateUser.setTelphone(user.getTelphone());
				updateUser.setMobile(user.getMobile());
				updateUser.setEMail(user.getEMail());
				updateUser.setQQ(user.getQQ());
				updateUser.setWeb(user.getWeb());
				updateUser.setAddress(user.getAddress());
				updateUser.setEnabled(user.getEnabled());
				updateUser.setPageStyle(user.getPageStyle());
				//获取选择的角色
				String userRoles = request.getParameter("userRoles");
				List<Role> roleList = new ArrayList<Role>();
				//如果为空，则默认为普通角色
				if(StringUtil.isEmpty(userRoles.trim())){
					Role role = roleService.getByName(getApplicationCommon("OrdinaryRole"));
					if(ObjectUtil.isNotNull(role)){
						roleList.add(role);
					}else{
						return WebResult.error("普通角色已不存在!");
					}
				}else{
					roleList = roleService.list(Restrictions.in("id", StringUtil.toIntArray(userRoles, ",")));
				}
				updateUser.setRoles(roleList);
				//获取选择的机构
				String userUnit = request.getParameter("userUnits");
				List<Unit> unitList = new ArrayList<Unit>();
				//如果为空，则默认为系统管理机构
				if(StringUtil.isEmpty(userUnit)){
					Unit unit = unitService.getByName(getApplicationCommon("SystemUnit"));
					if(ObjectUtil.isNotNull(unit)){
						unitList.add(unit);
					}else{
						return WebResult.error("系统管理机构已不存在!");
					}
				}else{
					unitList = unitService.list(Restrictions.in("id", StringUtil.toIntArray(userUnit, ",")));
				}
				updateUser.setUnits(unitList);
				
				//如果密码修改了，则需要重新加密成密文
				//否则无需加密
				if(!updateUser.getPassWord().equals(user.getPassWord())){
					//将密码加密
					DecUtil des = new DecUtil();
					des.genKey(ApplicationCommon.DEC_KEY);// 生成密匙
					updateUser.setPassWord(des.getEncString(updateUser.getPassWord()));
				}
				userService.update(updateUser);
				//记录日志
				logService.recordInfo("编辑用户","成功",getCurrentUser().getLoginName() , request.getRemoteAddr());
				return WebResult.success();
			}else{
				return WebResult.error("用户不存在，无法修改!");
			}
		}
	}
	/**
	 * 转向选择角色页面
	 * @return
	 */
	@RequestMapping(value="/role",method={RequestMethod.GET})
	public String role(Model model){
		model.addAttribute("defaultRole", getApplicationCommon("OrdinaryRole"));
		return "admin/system/user/role";
	}
	/**
	 * 转向选择机构单位页面
	 * @return
	 */
	@RequestMapping(value="/unit",method={RequestMethod.GET})
	public String unit(Model model){
		model.addAttribute("defaultUnit", getApplicationCommon("SystemUnit"));
		return "admin/system/user/unit";
	}
	/**
	 * 获取用户列表
	 * 如果角色ID不为空，则显示排除角色已拥有的用户
	 * 如果角色ID为空，则返回空集合
	 * @param pageBean
	 * @param name
	 * @param value
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value="/optionalUserListForRole",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> optionalUserListForRole(PageBean pageBean,String name,String value,@RequestParam(value="roleId",required=false)Integer roleId){
		Map<String,Object> userMap = new HashMap<String, Object>();
		if(ObjectUtil.isNotNull(roleId)){
			if(!StringUtil.isEmpty(name)){
				if("id".equals(name)){
					if(RegexUtil.isInteger(value))
						pageBean.addCriterion(Restrictions.eq(name, Integer.parseInt(value)));
				}else{
					pageBean.addCriterion(Restrictions.like(name, "%"+value+"%"));
				}
			}
			pageBean.addCriterion(Restrictions.not(Restrictions.eq("loginName", ApplicationCommon.SYSTEM_ADMIN)));
			//获取包含用户的角色信息
			Role role = roleService.getById_NoLazyUsers(roleId);
			if(ObjectUtil.isNotNull(role)){
				List<User> users = role.getUsers();
				if(users.size()>0){
					Integer[] userids = new Integer[users.size()];
					for(int i=0;i<users.size();i++){
						userids[i] = users.get(i).getId();
					}
					pageBean.addCriterion(Restrictions.not(Restrictions.in("id", userids)));
				}
				//判断是否是超级管理员，如果是超级管理员，能看到所有用户
				//如果不是超级管理员则只能看到本单位下(包括本单位)的用户
				if(!getCurrentUser().getLoginName().equals(ApplicationCommon.SYSTEM_ADMIN)){
					List<Unit> curentUserChildUnits = unitService.getAllChildUnitListHasSelf_NoLazyUser(getCurrentUser().getUnits().get(0).getId());
					pageBean.addCriterion(Restrictions.in("id", getUserIdsInUnitList(curentUserChildUnits)));
				}
				List<User> userList = userService.listByPage(pageBean);
				//ReflectUtil.removeLazyProperty(userList);
				userMap.put("rows", userList);
				userMap.put("total", pageBean.getTotalCount());
			}
		}else{
			userMap.put("total", 0);
			userMap.put("rows", "");
		}
		return userMap;
	}
	/**
	 * 获取用户列表
	 * 如果角色ID不为空，则显示排除角色已拥有的用户
	 * 如果角色ID为空，则返回空集合
	 * @param pageBean
	 * @param name
	 * @param value
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value="/roleUserListForRole",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> roleUserListForRole(PageBean pageBean,String name,String value,@RequestParam(value="roleId",required=false)Integer roleId){
		Map<String,Object> userMap = new HashMap<String, Object>();
		if(ObjectUtil.isNotNull(roleId)){
			if(!StringUtil.isEmpty(name)){
				if("id".equals(name)){
					if(RegexUtil.isInteger(value))
						pageBean.addCriterion(Restrictions.eq(name, Integer.parseInt(value)));
				}else{
					pageBean.addCriterion(Restrictions.like(name, "%"+value+"%"));
				}
			}
			pageBean.addCriterion(Restrictions.not(Restrictions.eq("loginName", ApplicationCommon.SYSTEM_ADMIN)));
			//获取包含用户的角色信息
			Role role = roleService.getById_NoLazyUsers(roleId);
			if(ObjectUtil.isNotNull(role)){
				List<User> users = role.getUsers();
				if(users.size()>0){
					Integer[] userids = new Integer[users.size()];
					for(int i=0;i<users.size();i++){
						userids[i] = users.get(i).getId();
					}
					pageBean.addCriterion(Restrictions.in("id", userids));
					//判断是否是超级管理员，如果是超级管理员，能看到所有用户
					//如果不是超级管理员则只能看到本单位下(包括本单位)的用户
					if(!getCurrentUser().getLoginName().equals(ApplicationCommon.SYSTEM_ADMIN)){
						List<Unit> curentUserChildUnits = unitService.getAllChildUnitListHasSelf_NoLazyUser(getCurrentUser().getUnits().get(0).getId());
						pageBean.addCriterion(Restrictions.in("id", getUserIdsInUnitList(curentUserChildUnits)));
					}
					List<User> userList = userService.listByPage(pageBean);
					//ReflectUtil.removeLazyProperty(userList);
					userMap.put("rows", userList);
					userMap.put("total", pageBean.getTotalCount());
				}else{
					userMap.put("total", 0);
					userMap.put("rows", "");
				}
			}else{
				userMap.put("total", 0);
				userMap.put("rows", "");
			}
		}else{
			userMap.put("total", 0);
			userMap.put("rows", "");
		}
		return userMap;
	}
	/**
	 * 获取用户列表
	 * 如果角色ID不为空，则显示排除角色已拥有的用户
	 * 如果角色ID为空，则返回空集合
	 * @param pageBean
	 * @param name
	 * @param value
	 * @param roleId
	 * @return
	 */
	@RequestMapping(value="/unitUserListForUnit",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> unitUserListForUnit(PageBean pageBean,String name,String value,@RequestParam(value="unitId",required=false)Integer unitId){
		Map<String,Object> userMap = new HashMap<String, Object>();
		if(ObjectUtil.isNotNull(unitId)){
			if(!StringUtil.isEmpty(name)){
				if("id".equals(name)){
					if(RegexUtil.isInteger(value))
						pageBean.addCriterion(Restrictions.eq(name, Integer.parseInt(value)));
				}else{
					pageBean.addCriterion(Restrictions.like(name, "%"+value+"%"));
				}
			}
			pageBean.addCriterion(Restrictions.not(Restrictions.eq("loginName", ApplicationCommon.SYSTEM_ADMIN)));
			//获取包含用户的角色信息
			Unit unit = unitService.getById_NoLazyUser(unitId);
			if(ObjectUtil.isNotNull(unit)){
				List<User> users = unit.getUsers();
				if(users.size()>0){
					Integer[] userids = new Integer[users.size()];
					for(int i=0;i<users.size();i++){
						userids[i] = users.get(i).getId();
					}
					pageBean.addCriterion(Restrictions.in("id", userids));
					List<User> userList = userService.listByPage(pageBean);
					//ReflectUtil.removeLazyProperty(userList);
					userMap.put("rows", userList);
					userMap.put("total", pageBean.getTotalCount());
				}else{
					userMap.put("total", 0);
					userMap.put("rows", "");
				}
			}else{
				userMap.put("total", 0);
				userMap.put("rows", "");
			}
		}else{
			userMap.put("total", 0);
			userMap.put("rows", "");
		}
		return userMap;
	}
	/**
	 * 锁定或者解锁用户
	 * @param ids
	 * @param 0 锁定，1解锁
	 * @return
	 */
	@RequestMapping(value="/lock",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject lockOrUnLockUser(Integer[] ids,int type){
		JSONObject jResult = new JSONObject();
		if(ObjectUtil.isNotEmpty(ids))
			jResult = userService.executeLockOrUnLockUser(ids, type);
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
	@RequestMapping(value="/toChangePass",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject toChangePass(){
		if(!isAllowAccess()){
			return WebResult.NeedVerifyPassword();
		}else{
			return WebResult.success();
		}
	}
	@RequestMapping(value="/changePass",method={RequestMethod.GET})
	public String changePass(){
		return "admin/system/user/changePass";
	}
	@RequestMapping(value="/doChangePass",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doChangePass(String passWord){
		if(!isAllowAccess()){
			return WebResult.NeedVerifyPassword();
		}else{
			User user = getCurrentUser();
			//判断密码是否与之前相同
			DecUtil des = new DecUtil();
			des.genKey(ApplicationCommon.DEC_KEY);
			passWord = des.getEncString(passWord);
			if(passWord.equals(user.getPassWord())){
				return WebResult.NoChange();
			}else{
				user.setPassWord(passWord);
				userService.update(user);
				return WebResult.success();
			}
		}
	}
}
