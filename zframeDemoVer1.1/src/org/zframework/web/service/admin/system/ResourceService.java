package org.zframework.web.service.admin.system;


import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.Condition;
import org.zframework.web.entity.system.Resource;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;

@Service
public class ResourceService extends BaseService<Resource>{
	@Autowired
	private LogService logService;
	/**
	 * 获取父目录下最后一个资源
	 * @param parentId
	 * @return
	 */
	public Resource getLastRes(int parentId){
		return baseDao.getBy(Resource.class, Condition.NEW().eq("parentId", parentId).desc("location"));
	}
	/**
	 * 获取Location
	 * @param ids
	 * @return
	 */
	public List<Integer> getLocations(Integer[] ids){
		List<Integer> locations = null;
		String hql = "select location from Resource where id in ("+StringUtil.toSameString("?", ids.length, ",")+")";
		locations = baseDao.listSingleColumn(Integer.class, hql, ids);
		return locations;
	}
	/**
	 * 执行排序
	 * @param parentId
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public boolean executeSort(Integer parentId,Integer[] ids) throws Exception{
		int successCount = 0;
		String hql = "update Resource set location=? where id=?";
		for(int i=0;i<ids.length;i++){
			Integer[] params = new Integer[]{i+1,ids[i]};
			baseDao.execteBulk(hql, params);
			successCount++;
		}
		if(successCount == ids.length)
			return true;
		throw new Exception("排序失败，事务回滚.");
	}
	/**
	 * 获取资源实例，立即获取所属按钮
	 * @param id
	 * @return
	 */
	public Resource getById_NoLazyButtons(Serializable id){
		Resource res = getById(id);
		if(ObjectUtil.isNotNull(res))
			Hibernate.initialize(res.getButtons());
		return res;
	}
	/**
	 * 删除资源
	 * @param ids
	 */
	public void deleteRes(Integer[] ids){
		String strIds = StringUtil.toString(ids);
		String hql = "delete Resource where id in ("+strIds+")";
		baseDao.execteBulk(hql, null);
		hql = "delete Resource where parentId in ("+strIds+")";
		baseDao.execteBulk(hql, null);
	}
	public List<Resource> list_noLazyButtons(Criterion[] criterions,Order...orders){
		List<Resource> list = list(criterions,orders);
		for(Resource res : list){
			Hibernate.initialize(res.getButtons());
		}
		return list;
	}
	/**
	 * 执行编辑操作
	 * @param request
	 * @param res
	 * @param result
	 * @param user
	 * @return
	 */
	public JSONObject executeEdit(HttpServletRequest request, Resource res,User user) {
		Resource eqRes = getById(res.getId());
		if(ObjectUtil.isNotNull(eqRes)){
			if(ObjectUtil.equalProperty(res, eqRes, new String[]{"id","parentId","name","url","icon","descript","enabled"})){
				return WebResult.NoChange();
			}else if(ObjectUtil.isNotNull(this.get(Restrictions.eq("name", res.getName()),Restrictions.eq("parentId", eqRes.getParentId()),Restrictions.not(Restrictions.eq("id", res.getId()))))){
				logService.recordInfo("编辑资源","资源名称已经存在", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("资源名称已经存在!");
			}else{
				eqRes.setParentId(res.getParentId());
				eqRes.setName(res.getName());
				eqRes.setUrl(res.getUrl());
				eqRes.setIcon(res.getIcon());
				eqRes.setDescript(res.getDescript());
				eqRes.setEnabled(res.getEnabled());
				
				update(eqRes);
				//更新当前用户的资源列表信息
				List<Resource> resList = user.getResources();
				int cIdx = -1;
				for(int i=0;i<resList.size();i++){
					Resource r = resList.get(i);
					if(eqRes.getId() == r.getId()){
						cIdx =i;
						break;
					}
				}
				if(cIdx != -1){
					user.getResources().remove(cIdx);
					user.getResources().add(eqRes);
				}
				//记录日志
				logService.recordInfo("编辑资源","成功", user.getLoginName(), request.getRemoteAddr());
				return WebResult.success();
			}
		}else{
			logService.recordInfo("编辑资源","失败(尝试编辑不存在的资源)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error(ControllerCommon.UNAUTHORIZED_ACCESS);
		}
	}
	/**
	 * 执行新增
	 * @param request
	 * @param res
	 * @param result
	 * @param user
	 * @return
	 */
	public JSONObject executeAdd(HttpServletRequest request, Resource res,User user) {
		Resource eqRes = get(Restrictions.eq("name", res.getName()),Restrictions.eq("parentId", res.getParentId()));
		if(ObjectUtil.isNotNull(eqRes)){
			logService.recordInfo("新增资源","失败(资源已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("此资源已经存在!");
		}else{
			//设置location
			Resource lastRes = getLastRes(res.getParentId());
			if(ObjectUtil.isNotNull(lastRes))
				res.setLocation(lastRes.getLocation()+1);
			else
				res.setLocation(1);
			save(res);
			logService.recordInfo("新增资源","成功", user.getLoginName(), request.getRemoteAddr());
			return WebResult.success();
		}
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
			hql = "update Resource set enabled=1 where id in ("+StringUtil.toSameString("?", ids.length, ",")+")";
		}else if(type == 1){
			resultTag = "isUnLocked";
			hql = "update Resource set enabled=0 where id in ("+StringUtil.toSameString("?", ids.length, ",")+")";
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
