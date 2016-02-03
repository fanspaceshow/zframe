package org.zframework.web.service.admin.system;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.orm.dao.BaseHibernateDao;
import org.zframework.web.controller.admin.system.RoleController;
import org.zframework.web.entity.system.Resource;
import org.zframework.web.entity.system.Role;
import org.zframework.web.entity.system.RoleResourceButton;
import org.zframework.web.entity.system.User;

@Service
public class LoginService {
	@Autowired
	private BaseHibernateDao baseDao;
	/**
	 * 登陆验证
	 * 获取用户所有角色信息
	 * 获取用户所有资源
	 * @param username
	 * @param password
	 * @return
	 */
	public User login(String username,String password){
		User user = baseDao.getBy(User.class, Restrictions.eq("loginName", username));
		if(user!=null){
			//初始化用户角色和资源信息
			if(user.getRoles().size()>0){
				List<Integer> roleids = new ArrayList<Integer>();
				boolean isSystemRole = false;
				for(Role role : user.getRoles()){
					if(role.getName().equals(ApplicationCommon.SYSCOMMONS.get("SystemRole"))){
						isSystemRole = true;
						break;
					}
					roleids.add(role.getId());
				}
				if(isSystemRole){//系统管理员拥有全部资源权限，所有按钮权限
					user.setResources(baseDao.list(Resource.class, new Criterion[]{Restrictions.eq("enabled", 0)},new Order[]{Order.asc("location"),Order.asc("parentId")}));
					user.setResourcesBtns(user.getResources());
				}else{
					//获取用户拥有的资源信息
					List<Resource> resources = baseDao.listByNativeSQL(Resource.class, "select * from sys_resource where enabled=0 and id in (select resourceid from sys_roleresource where roleid in ("+StringUtil.toString(roleids)+")) order by location asc,parentId asc");
					//保存用户的资源权限，按钮为资源所拥有的按钮
					for(Resource res : resources){
						Hibernate.initialize(res.getButtons());
					}
					//ReflectUtil.removeLazyProperty(resources,"buttons");
					user.setResources(resources);
					
					//保存用户的资源权限，按钮为用户角色所拥有的按钮权限
					List<RoleResourceButton> rrbList = baseDao.list(RoleResourceButton.class,Restrictions.in("roleId", roleids.toArray(new Integer[]{})));
					RoleController rc = new RoleController();
					List<Resource> userResources = new ArrayList<Resource>();
					for(Resource res : resources){
						Resource eqRes = (Resource) ObjectUtil.clone(res);
						eqRes.setButtons(rc.getResourceButtons(rrbList, eqRes));
						Hibernate.initialize(eqRes.getButtons());
						userResources.add(eqRes);
					}
					//ReflectUtil.removeLazyProperty(userResources,"buttons");
					user.setResourcesBtns(userResources);
				}
			}
			//初始化用户单位信息
			Hibernate.initialize(user.getUnits());
		}
		return user;
	}
	public void updateLastInfo(User user,String ip){
		user.setLastLoginIP(ip);
		user.setLastLoginType("1");//WEB网页形式
		user.setLastLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
		baseDao.update(user);
	}
	/**
	 * 锁定用户
	 * @param user
	 */
	public void lockUser(User user){
		user.setEnabled(1);
		baseDao.update(user);
	}
}
