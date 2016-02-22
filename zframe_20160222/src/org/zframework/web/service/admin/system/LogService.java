package org.zframework.web.service.admin.system;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.PageBean;
import org.zframework.web.entity.system.Log;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;

/**
 * 记录日志到数据库中
 * 数据库中type字段值对应
 *    1:info
 *    2:debug
 *    3:error
 * @author ZENGCHAO 
 *
 */
@Service
public class LogService extends BaseService<Log>{

	private final Logger logger = Logger.getLogger(this.getClass());
	/**
	 * 记录日常日志
	 * @param msg
	 * @param username
	 * @param request
	 */
	public void recordInfo(String mname,String msg,String username,String ip){
		Throwable ex = new Throwable();
		StackTraceElement[] stackElements = ex.getStackTrace();
		int index = 0;
		for(int i=0;i<stackElements.length;i++){
			StackTraceElement e= stackElements[i];
			if(e.isNativeMethod()){
				index = i-1;
				break;
			}
		}
		String classname=stackElements[index].getClassName();
		classname=getClassName(classname);

		Log log = new Log();
		log.setType(1);
		log.setManipulateName(mname);
		log.setContent(msg);
		log.setUserName(username);
		if("0:0:0:0:0:0:0:1".equals(ip))
			ip = "localhost";
		log.setIP(ip);
		log.setTime(new Date());
		log.setClassName(classname);
		baseDao.save(log);
		
	}
	/**
	 * 记录调试日志
	 * @param msg
	 * @param username
	 * @param request
	 */
	public void recordDebug(String mname,String msg,String username,String ip){
		Throwable ex = new Throwable();
		StackTraceElement[] stackElements = ex.getStackTrace();
		int index = 0;
		for(int i=0;i<stackElements.length;i++){
			StackTraceElement e= stackElements[i];
			if(e.isNativeMethod()){
				index = i-1;
				break;
			}
		}
		String classname=stackElements[index].getClassName();
		classname=getClassName(classname);
		Log log = new Log();
		log.setType(2);
		log.setContent(msg);
		log.setUserName(username);
		log.setIP(ip);
		log.setTime(new Date());
		log.setClassName(classname);
		log.setManipulateName(mname);
		baseDao.save(log);
	}
	/**
	 * 记录错误日志
	 * @param msg
	 * @param username
	 * @param request
	 */
	public void recordError(String mname,String msg,String username,String ip){
		Throwable ex = new Throwable();
		StackTraceElement[] stackElements = ex.getStackTrace();
		int index = 0;
		for(int i=0;i<stackElements.length;i++){
			StackTraceElement e= stackElements[i];
			if(e.isNativeMethod()){
				index = i-1;
				break;
			}
		}
		String classname=stackElements[index].getClassName();
		classname=getClassName(classname);
		Log log = new Log();
		log.setType(3);
		log.setUserName(username);
		log.setIP(ip);
		log.setContent(msg);
		log.setTime(new Date());
		log.setClassName(classname);
		log.setManipulateName(mname);
		baseDao.save(log);
	}
	/***
	 * 调用当前栈的属性获取上一级调用日志方法的类名。
	 * 获取的字符串是org.xx.xx.xx
	 * @param canme 类名
	 * */
	  private String getClassName(String cname){
		return cname.substring(cname.lastIndexOf(".")+1, cname.length());
	}
	  /***
	   * 分页获取日志对象
	   * */
	  public Map<String, Object> getLogList(PageBean pageBean, String name,
				String value) {
			Map<String,Object> logMap = new HashMap<String, Object>();
			if(!StringUtil.isEmpty(name)){
				if(StringUtil.isInteger(value))
					pageBean.addCriterion(Restrictions.eq(name, new Integer(value)));
				else
					pageBean.addCriterion(Restrictions.like(name, "%"+value+"%"));
			}
			List<Log> logList=this.listByPage(pageBean);
			logMap.put("rows", logList);
			logMap.put("total", pageBean.getTotalCount());
			return logMap;
		}
	  /***
	   * 删除日志
	   * */
		public JSONObject executeDelete(Integer[] ids,User user) {
			this.delete(ids);
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd "); 
			logger.info("删除日志。操作人:"+user.getLoginName()+"。操作时间:"+sdf.format(new Date())+"");
			return WebResult.success();
		}

}
