package org.zframework.web.service.admin.system;

import java.io.Serializable;

import net.sf.json.JSONObject;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.orm.dao.BaseHibernateDao;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;

@Service
public class UserService extends BaseService<User>{
	@Autowired
	private BaseHibernateDao baseDao;
	/**
	 * 根据用户名获取用户
	 * @param loginName
	 * @return
	 */
	public User getByLoginName(String loginName){
		return baseDao.getBy(User.class, Restrictions.eq("loginName", loginName));
	}
	/**
	 * 根据Id获取用户
	 * 延迟加载属性立即加载
	 * @param id
	 * @return
	 */
	public User getById_NoLazy(Serializable id){
		User user = getById(id);
		if(ObjectUtil.isNotNull(user)){
			Hibernate.initialize(user.getRoles());
			Hibernate.initialize(user.getUnits());
		}
		return user;
	}
	public JSONObject executeLockOrUnLockUser(Integer[] ids,int type){
		JSONObject jResult = new JSONObject();
		String hql = null;
		String resultTag = "isLocked";
		if(type == 0){
			hql = "update User set enabled=1 where id in ("+StringUtil.toSameString("?", ids.length, ",")+")";
		}else if(type == 1){
			resultTag = "isUnLocked";
			hql = "update User set enabled=0 where id in ("+StringUtil.toSameString("?", ids.length, ",")+")";
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
