package org.zframework.web.service.admin.system;

import org.springframework.stereotype.Service;
import org.zframework.web.entity.system.RoleResourceButton;
import org.zframework.web.service.BaseService;

@Service
public class RoleResBtnService extends BaseService<RoleResourceButton>{
	/**
	 * 根据角色ID删除关联的资源按钮数据
	 * @param roleId
	 */
	public void deleteByRoleId(Integer roleId){
		String hql = "delete RoleResourceButton where roleId=?";
		baseDao.execteBulk(hql, new Object[]{roleId});
	}
}
