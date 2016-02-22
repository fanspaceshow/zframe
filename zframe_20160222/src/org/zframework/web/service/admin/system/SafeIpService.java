package org.zframework.web.service.admin.system;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.entity.system.SafeIp;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;

@Service
public class SafeIpService extends BaseService<SafeIp>{
	@Autowired
	private LogService logService;
	/**
	 * 初始化IP
	 */
	public synchronized void initIp(){
		ApplicationCommon.IP_LIST.clear();
		ApplicationCommon.IP_LIST.addAll(baseDao.listSingleColumn(String.class,"select ip from SafeIp where enabled=?",new Object[]{0}));
	}
	public JSONObject executeAdd(HttpServletRequest request, SafeIp safeIp,User user) {
		SafeIp eqIp = getByProperties("ip", safeIp.getIp());
		if(ObjectUtil.isNotNull(eqIp)){
			logService.recordInfo("新增IP","失败（IP已经存在）",user.getLoginName() , request.getRemoteAddr());
			return WebResult.error("此IP已经存在");
		}else{
			save(safeIp);
			initIp();
			logService.recordInfo("新增IP","成功",user.getLoginName() , request.getRemoteAddr());
			return WebResult.success();
		}
	}
	/**
	 * 执行删除操作
	 * @param request
	 * @param ids
	 * @param user
	 * @return
	 */
	public JSONObject executeDelete(HttpServletRequest request,Integer[] ids,User user) {
		JSONObject jResult = new JSONObject();
		//记录日志
		List<SafeIp> ipList = getByIds(ids);
		for(SafeIp ip : ipList){
			if(ObjectUtil.isNull(ip)){
				jResult = WebResult.error("尝试删除不存在的IP");
				logService.recordInfo("删除安全配置IP地址","失败（尝试删除不存在的IP）",user.getLoginName() , request.getRemoteAddr());
				break;
			}else{
				delete(ip);
				logService.recordInfo("删除安全配置IP地址","成功（IP地址ID:"+ip.getId()+",IP地址:"+ip.getIp()+")",user.getLoginName() , request.getRemoteAddr());
				return WebResult.success();
			}
		}
		initIp();
		return jResult;
	}
	/**
	 * 编辑IP
	 * @param request
	 * @param safeIp
	 * @param user
	 * @return
	 */
	public JSONObject executeEdit(HttpServletRequest request, SafeIp safeIp,User user) {
		SafeIp eqIp = getById(safeIp.getId());
		if(ObjectUtil.isNull(eqIp)){
			logService.recordInfo("编辑安全配置IP地址","失败（尝试编辑不存在的IP）",user.getLoginName() , request.getRemoteAddr());
			return WebResult.error("IP地址已不存在!");
		}else if(ObjectUtil.isNotNull(get(Restrictions.eq("ip", safeIp.getIp()),Restrictions.not(Restrictions.eq("id", safeIp.getId()))))){
			logService.recordInfo("编辑安全配置IP地址","失败（尝试编辑为已经存在的IP）",user.getLoginName() , request.getRemoteAddr());
			return WebResult.error("IP地址已存在!");
		}else{
			eqIp.setIp(safeIp.getIp());
			eqIp.setEnabled(safeIp.getEnabled());
			update(eqIp);
			initIp();
			logService.recordInfo("编辑安全配置IP地址","成功",user.getLoginName() , request.getRemoteAddr());
			return WebResult.success();
		}
	}
	public JSONObject executeEnableOrDisable(HttpServletRequest request,int type, Integer[] ids, User currentUser) {
		JSONObject jResult = new JSONObject();
		String tag = "启用";
		String resultTag = "isEnabled";
		if(type == 1){
			tag = "禁用";
			resultTag = "isDisabled";
		}
		if(ObjectUtil.isNotEmpty(ids)){
			String hql = "update SafeIp set enabled = "+(type==0?0:1)+" where id in("+StringUtil.toSameString("?", ids.length, ",")+")";
			int result = baseDao.execteBulk(hql, ids);
			if(result>0){
				initIp();
				jResult.element(resultTag, true);
			}else{
				jResult.element(resultTag, false);
				jResult.element("error", tag+"失败，请稍后重试!");
			}
		}else{
			jResult.element(resultTag, false);
			jResult.element("error", "非法操作!");
		}
		return jResult;
	}
}
