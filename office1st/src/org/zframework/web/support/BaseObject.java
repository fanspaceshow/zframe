package org.zframework.web.support;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.web.support.WebContextHelper;
import org.zframework.orm.cache.ICacheProvider;
import org.zframework.orm.support.IDataDict;
import org.zframework.web.entity.system.User;

/**
 * web包中所有类的父类
 * @author zengchao
 *
 */
public class BaseObject{
	/**
	 * 获取数据字典
	 * @return
	 */
	public IDataDict getDataDict(){
		return ApplicationCommon.DATADICT;
	}
	/**
	 * 获取系统缓存提供者
	 * @return
	 */
	public ICacheProvider getApplicationCache(){
		return ApplicationCommon.APPLICATIONCACHE;
	}
	
	/**
	 * 获取系统配置信息
	 * @param key
	 * @return
	 */
	public String getApplicationCommon(String key){
		return ApplicationCommon.SYSCOMMONS.get(key);
	}
	
	/**
	 * 获取当前登录用户
	 * 不存在返回Null
	 * @return
	 */
	public User getCurrentUser(){
		return WebContextHelper.getSession()==null?null:(User)WebContextHelper.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
	}
	/**
	 * 获取请求对象
	 * @return
	 */
	public HttpServletRequest getRequest(){
		return WebContextHelper.getSession()==null?null:WebContextHelper.getRequest();
	}
	/**
	 * 获取当前时间 格式为 yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public String getCurrentTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	/**
	 * 获取当前时间 格式为 pattern
	 * @param pattern 时间格式
	 * @return
	 */
	public String getCurrentTime(String pattern){
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(new Date());
	}
	/**
	 * 获取访问着的IP地址
	 * @return
	 */
	public String getRequestAddr(){
		String ip = getRequest().getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
	        ip = getRequest().getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
	        ip = getRequest().getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 ||"unknown".equalsIgnoreCase(ip)) {
	        ip = getRequest().getRemoteAddr();
	    }
	    return ip;
	}
	/**
	 * 判断集合中是否确定的属性及属性值
	 * @param array
	 * @param propertyName
	 * @param propertyValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean hasPropertyValueInArray(Object array,Object propertyName,Object propertyValue){
		if(array instanceof JSONArray){
			JSONArray jArray = (JSONArray) array;
			int len = jArray.size();
			for(int i=0;i<len;i++){
				JSONObject jObj = jArray.getJSONObject(i);
				if(propertyValue.equals(jObj.get(propertyName))){
					return true;
				}
			}
		}else if(array instanceof List<?>){
			List<JSONObject> jList = (List<JSONObject>) array;
			for(JSONObject jObj : jList){
				if(propertyValue.equals(jObj.get(propertyName))){
					return true;
				}
			}
		}
		return false;
	}
}
