package org.zframework.web.service.admin.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.entity.system.Resource;
import org.zframework.web.entity.system.Role;
import org.zframework.web.entity.system.RoleResourceButton;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;

@Service
public class RoleService extends BaseService<Role>{
	@Autowired
	private RoleResBtnService rrbService;
	@Autowired
	private ResourceService resService;
	@Autowired
	private LogService logService;
	@Autowired
	private UserService userService;
	/**
	 * 获取角色 
	 * 强制加载资源
	 * @param id
	 * @return
	 */
	public Role getById_NoLazyResource(Integer id){
		Role role = getById(id);
		if(ObjectUtil.isNotNull(role))
			//加载资源属性
			Hibernate.initialize(role.getResources());
		return role;
	}
	/**
	 * 获取角色 
	 * 强制加载资源和资源下属按钮
	 * @param id
	 * @return
	 */
	public Role getById_NoLazyResourceAndButtons(Integer id){
		Role role = getById(id);
		if(ObjectUtil.isNotNull(role)){
			//加载资源属性
			Hibernate.initialize(role.getResources());
			for(Resource res : role.getResources()){
				Hibernate.initialize(res.getButtons());
			}
		}
		return role;
	}
	/**
	 * 获取角色
	 * 强制加载用户列表
	 * @param id
	 * @return
	 */
	public Role getById_NoLazyUsers(Integer id){
		Role role = getById(id);
		if(ObjectUtil.isNotNull(role)){
			//加载用户资源
			Hibernate.initialize(role.getUsers());
		}
		return role;
	}
	/**
	 * 编辑角色
	 * @param request
	 * @param role
	 * @param result
	 * @return
	 */
	public JSONObject executeEdit(HttpServletRequest request,User user, Role role,
			BindingResult result) {
		JSONObject jResult = new JSONObject();
		//记录日志
		if(result.hasErrors()){
			jResult.element("isEdited", false);
			jResult.element("error", "请按要求填写表单!");
			logService.recordInfo("编辑角色","失败(非法提交表单!)",user.getLoginName() , request.getRemoteAddr());
		}else{
			Role eqRole = getById(role.getId());
			if(ObjectUtil.isNull(eqRole)){
				jResult.element("isEdited", false);
				jResult.element("error", "该角色已不存在!");
				logService.recordInfo("编辑角色","失败(角色不存在!)",user.getLoginName() , request.getRemoteAddr());
			}else if(ObjectUtil.isNotNull(this.get(Restrictions.eq("name", role.getName()),Restrictions.not(Restrictions.eq("id", eqRole.getId()))))){
				jResult.element("isEdited", false);
				jResult.element("error", "该角色名已存在!");
				logService.recordInfo("编辑角色","失败(角色不存在!)",user.getLoginName() , request.getRemoteAddr());
			}else{
				if(ObjectUtil.isNull(eqRole.getDescript())){
					eqRole.setDescript("");
				}
				if(ObjectUtil.equalProperty(role, eqRole, new String[]{"name","type","enabled","descript"})){
					jResult.element("NoChanges", true);
				}else{
					//设置要编辑的字段
					if(!role.getName().equals(getApplicationCommon("SystemRole")) && !role.getName().equals(getApplicationCommon("OrdinaryRole"))){
						eqRole.setName(role.getName());
						eqRole.setType(role.getType());
						eqRole.setEnabled(role.getEnabled()==0);
					}
					eqRole.setDescript(role.getDescript());
					//更新操作
					update(eqRole);
					jResult.element("isEdited", true);
					logService.recordInfo("编辑角色","成功",user.getLoginName() , request.getRemoteAddr());
				}
			}
		}
		return jResult;
	}
	/**
	 * 执行分配资源
	 * @param 角色Id
	 * @param res
	 * @return
	 */
	public JSONObject executeAssignResource(HttpServletRequest request,User user,Integer roleid, String res) {
		Role role = getById_NoLazyResource(roleid);
		if(ObjectUtil.isNotNull(role) && !role.getName().equals(getApplicationCommon("SystemRole"))){
			JSONObject jRes = JSONObject.fromObject(res);
			Set<?> resids = jRes.keySet();
			//修改后的角色资源集合
			List<Resource> roleResource = new ArrayList<Resource>();
			//删除之前的角色按钮数据
			
			rrbService.deleteByRoleId(roleid);
			for(Object resid : resids){
				Resource resource = resService.getById(Integer.parseInt(resid.toString()));
				roleResource.add(resource);
				if(resource.getParentId()!=0){
					//标识是否是父节点
					boolean isNotFirstParent = true;
					while(isNotFirstParent){
						Resource parentRes = resService.getById(resource.getParentId());
						roleResource.add(parentRes);
						if(parentRes.getParentId()==0)
							isNotFirstParent = false;
					}
				}
				//新增按钮数据到数据库
				String btnIds = jRes.getString(resid.toString());
				if(!StringUtil.isEmpty(btnIds)){
					String[] btnIdArray = btnIds.split(",");
					for(String btnid : btnIdArray){
						RoleResourceButton rrb = new RoleResourceButton();
						rrb.setRoleId(roleid);
						rrb.setResourceId(resource.getId());
						rrb.setButtonId(Integer.parseInt(btnid));
						//保存到数据库中
						rrbService.save(rrb);
					}
				}
			}
			role.setResources(roleResource);
			update(role);
			logService.recordInfo("角色分配资源","成功",user.getLoginName() , request.getRemoteAddr());
			return WebResult.success();
		}else{
			logService.recordInfo("角色分配资源","失败",user.getLoginName() , request.getRemoteAddr());
			return WebResult.error("非访访问!");
		}
	}
	/**
	 * 执行分配用户到角色
	 * @param request
	 * @param user
	 * @param roleid
	 * @param userIds
	 * @param 1=从角色中移除用户 2=添加用户到角色（复制），3添加用户到角色（移动）
	 * @return
	 */
	public JSONObject executeAssignUser(HttpServletRequest request,User user,Integer roleid,Integer[] userIds,int type){
		Role role = getById_NoLazyUsers(roleid);
		if(ObjectUtil.isNotNull(role)){
			List<User> users = role.getUsers();
			if(type == 1){//执行移除用户
				//新用户列表
				List<User> newUsers = new ArrayList<User>();
				newUsers.addAll(users);
				if(users.size()>0){
					boolean hasError = false;
					boolean hasChanged = false;
					StringBuffer errorInfo = new StringBuffer();
					for(User u : users){
						//判断用户是否只拥有一个角色
						if(hasUserInArray(u,userIds)){//判断角色的用户是否在选中的用户列表中
							if(isOnlyOneRoleUser(u)){
								errorInfo.append("【"+u.getRealName()+"】");
								hasError = true;
								continue;
							}
							newUsers.remove(u);
							hasChanged = true;
						}
					}
					if(hasChanged){
						//设置新的用户列表
						role.setUsers(newUsers);
						//更新到数据库中
						update(role);
					}
					if(hasError){//如果存在错误
						logService.recordInfo("删除角色中用户","成功(个别用户无法移除)",user.getLoginName() , request.getRemoteAddr());
						return WebResult.error(errorInfo.append("只拥有一个角色，不可移除!").toString());
					}else{
						logService.recordInfo("删除角色中用户","成功",user.getLoginName() , request.getRemoteAddr());
						if(hasChanged)
							return WebResult.success();
						else
							return WebResult.NoChange();
					}
					
				}else{
					logService.recordInfo("删除角色中用户","失败(选中用户集合为空!)",user.getLoginName() , request.getRemoteAddr());
					return WebResult.error("选中的用户集合为空!");
				}
			}else if(type == 2){
				List<User> newUsers = userService.list(Restrictions.in("id", userIds));
				users.addAll(newUsers);
				//更新到数据库
				update(role);
				logService.recordInfo("复制用户到角色","成功",user.getLoginName() , request.getRemoteAddr());
				return WebResult.success();
			}else if(type == 3){//移动用户到角色
				//删除用户的其他角色信息
				List<User> newUsers = userService.list(Restrictions.in("id", userIds));
				for(User u : newUsers){
					u.setRoles(new ArrayList<Role>());
					userService.update(u);
				}
				users.addAll(newUsers);
				//更新到数据库
				update(role);
				logService.recordInfo("移动用户到角色","成功",user.getLoginName() , request.getRemoteAddr());
				return WebResult.success();
			}
		}else{
			if(type==1)
				logService.recordInfo("删除角色中用户","失败(角色不存在!)",user.getLoginName() , request.getRemoteAddr());
			else
				logService.recordInfo("添加用户到角色","失败(角色不存在!)",user.getLoginName() , request.getRemoteAddr());
			return WebResult.error("角色不存在!");
		}
		return WebResult.error("错误!");
	}
	/**
	 * 判断此用户是否在用户ID列表中
	 * @param user
	 * @param userids
	 * @return
	 */
	private boolean hasUserInArray(User user,Integer[] userids){
		boolean result = false;
		for(Integer id : userids){
			if(user.getId().equals(id)){
				result = true;
				break;
			}
		}
		return result;
	}
	/**
	 * 判断用户是否只有一个角色，并且
	 * @param user
	 * @return
	 */
	private boolean isOnlyOneRoleUser(User user){
		Hibernate.initialize(user.getRoles());
		if(user.getRoles().size()>1)
			return false;
		return true;
	}
	/**
	 * 锁定
	 * @param ids
	 * @param type
	 * @return
	 */
	public JSONObject executeLockOrUnLockUser(Integer[] ids,int type){
		JSONObject jResult = new JSONObject();
		String hql = null;
		String resultTag = "isLocked";
		if(type == 0){
			hql = "update Role set enabled=1 where id in ("+StringUtil.toSameString("?", ids.length, ",")+")";
		}else if(type == 1){
			resultTag = "isUnLocked";
			hql = "update Role set enabled=0 where id in ("+StringUtil.toSameString("?", ids.length, ",")+")";
		}
		if(ObjectUtil.isNotNull(hql)){
			int result = baseDao.execteBulk(hql, ids);
			if(result>0){
				jResult.element(resultTag, true);
			}else{
				jResult.element(resultTag, false);
				jResult.element("error", "操作失败，请稍后重试!");
			}
		}else{
			jResult.element(resultTag, false);
			jResult.element("error", "非法操作!");
		}
		return jResult;
	}
}
