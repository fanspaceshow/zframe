package org.zframework.web.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.criterion.Restrictions;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.WebContextHelper;
import org.zframework.orm.query.PageBean;
import org.zframework.web.entity.system.Role;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
import org.zframework.web.support.BaseObject;

/**
 * 控制类的父类
 * 一般用于放置一些controller类里面用到的逻辑方法
 * @author ZENGCHAO
 *
 * @param <M>
 */
public class BaseController<M> extends BaseObject{
	protected final String SUCCESS = "_result/_success";
	protected final String ERROR = "_result/_error";
	protected final String INFO = "_result/_info";
	
	private User currentUser = null;
	
	/**
	 * 当前用户是否是超级管理员
	 * @return
	 */
	public boolean isSuperadmin(){
		boolean isSuperadmin = false;
		this.currentUser = WebContextHelper.getSession()==null?null:(User)WebContextHelper.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
		if(ObjectUtil.isNotNull(currentUser)){
			if(currentUser.getLoginName().equals(ApplicationCommon.SYSTEM_ADMIN)){
				isSuperadmin = true;
			}else{
				isSuperadmin = false;
			}
		}
		return isSuperadmin;
	}
	/**
	 * 当前用户是否是系统管理员
	 * 权限低于超级管理员
	 * @return
	 */
	public boolean isSystemadmin(){
		boolean isSystemadmin = false;
		this.currentUser = WebContextHelper.getSession()==null?null:(User)WebContextHelper.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
		if(ObjectUtil.isNotNull(currentUser)){
			if(isSuperadmin()){//如果是超级管理员，也就拥有了系统管理员的角色
				isSystemadmin = true;
			}else{
				//判断角色列表中是否拥有系统管理员角色
				for(Role role : this.currentUser.getRoles()){
					if(role.getName().equals(getApplicationCommon("SystemRole"))){
						isSystemadmin = true;
						break;
					}
				}
			}
		}
		return isSystemadmin;
	}
	/**
	 * 判断是否通过密码验证
	 * 5分钟之后此次验证就将失效
	 * @return
	 */
	public boolean isAllowAccess(){
		Object obj = WebContextHelper.getSession().getAttribute(ApplicationCommon.ALLOW_ACCESS);
		if(ObjectUtil.isNotNull(obj)){//判断是否是第一次验证，第一次验证时session中应该是没有AllowAccess的值
			Boolean allowAccess = (Boolean) obj;
			//获取上次验证的时间
			Date dTimeSpan = (Date)WebContextHelper.getSession().getAttribute(ApplicationCommon.ALLOW_ACCESS_TIMESPAN);
			//判断是否超过5分钟
			Date dCurrent = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(dTimeSpan);
			cal.add(Calendar.MINUTE, 5);
			if(cal.getTime().before(dCurrent)){//超过时间
				//使上次验证失效
				WebContextHelper.getSession().setAttribute(ApplicationCommon.ALLOW_ACCESS, false);
			}
			return allowAccess;
		}else{
			return false;
		}
	}
	/**
	 * 分页获取集合
	 * @param pageBean 分页Bean 包含查询条件，排序条件
	 * @param name 搜索用 字段名称
	 * @param value 搜索用 字段值
	 * @param BaseServiceDao<M> 查询用Service实现类
	 * @return
	 */
	public <T> Map<String,Object> list(PageBean pageBean,String name,String value,BaseService<T> bsd){
		Map<String,Object> map = new HashMap<String, Object>();
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value))
					pageBean.addCriterion(Restrictions.idEq(new Integer(value)));
				else
					pageBean.addCriterion(Restrictions.idEq(value));
			}else{
				if(RegexUtil.isInteger(value))
					pageBean.addCriterion(Restrictions.like(name,new Integer(value)));
				else
					pageBean.addCriterion(Restrictions.like(name, "%"+value+"%"));
			}
		}
		List<T> safeIpList = bsd.listByPage(pageBean);
		//ReflectUtil.removeLazyProperty(safeIpList);
		map.put("rows", safeIpList);
		map.put("total", pageBean.getTotalCount());
		return map;
	}
	/**
	 * 分页获取集合
	 * @param pageBean 分页Bean 包含查询条件，排序条件
	 * @param name 搜索用 字段名称数组
	 * @param value 搜索用 字段值数组
	 * @param BaseServiceDao<M> 查询用Service实现类
	 * @return
	 */
	public <T> Map<String,Object> list(PageBean pageBean,String[] name,String[] value,BaseService<T> bsd){
		Map<String,Object> btnMap = new HashMap<String, Object>();
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
		List<T> safeIpList = bsd.listByPage(pageBean);
		//ReflectUtil.removeLazyProperty(safeIpList);
		btnMap.put("rows", safeIpList);
		btnMap.put("total", pageBean.getTotalCount());
		return btnMap;
	}
	/**
	 * 转换表单验证消息。
	 * @param result
	 * @return
	 */
	public String convertBindingResultToString(BindingResult result){
		StringBuffer message = new StringBuffer("\n");
		List<FieldError> errorList = result.getFieldErrors();
		for(FieldError e : errorList){
			message.append(e.getField() + e.getDefaultMessage()+"\n");
		}
		return message.toString();
	}
	/**
	 * 文件下载
	 * @param request
	 * @param response
	 * @param 文件类型
	 * @param 服务器存放目录，相对路径
	 * @param 文件
	 */
	public void fileDownLoad(HttpServletRequest request,HttpServletResponse response,String contentType,File file){
		response.setContentType(contentType);
		String codedFileName = null;
		OutputStream fos = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		try {
			if(!file.exists())
				return;
			//以流的形式下载
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			codedFileName = java.net.URLEncoder.encode(file.getName(),"UTF-8");
			//清空response
			response.reset();
			response.setHeader("content-disposition", "attachment;filename=" + codedFileName);
			response.addHeader("Content-Length", "" + file.length());
			fos = new BufferedOutputStream(response.getOutputStream());
			fos.write(buffer);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(ObjectUtil.isNotNull(fos)){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(ObjectUtil.isNotNull(fis)){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(ObjectUtil.isNotNull(bis)){
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
