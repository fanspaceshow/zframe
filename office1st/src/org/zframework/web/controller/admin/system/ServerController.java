package org.zframework.web.controller.admin.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.factory.config.PropertyPlaceholderConfigurerExt;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.support.ApplicationContextHelper;
import org.zframework.web.controller.BaseController;

@Controller
@RequestMapping("/admin/server")
public class ServerController extends BaseController<Object>{
	public static String startTime = "";
	@RequestMapping(method={RequestMethod.GET})
	public String index(Model model){
		return "admin/system/server/index";
	}
	
	/**
	 * 获取应用软件信息
	 * @return
	 */
	@RequestMapping(value="/app",method={RequestMethod.POST})
	@ResponseBody
	public JSONArray appInfo(){
		JSONArray jArray = new JSONArray();
		List<String> keys = new ArrayList<String>();
		keys.add("应用代码");
		keys.add("应用名称");
		keys.add("应用版本");
		keys.add("本次启动时间");
		keys.add("最后更新时间");
		keys.add("当前已登录用户数");
		keys.add("是否是调试模式");
		List<String> values = new ArrayList<String>();
		values.add("ZFRAME");
		values.add("系统框架");
		values.add("alpha2");
		values.add(startTime);
		values.add("安装后未曾更新");
		values.add(ApplicationCommon.LOGIN_USERS.size()+"");
		values.add("true");
		
		for(int i = 0;i<keys.size();i++){
			JSONObject jObj = new JSONObject();
			jObj.element("name", keys.get(i));
			jObj.element("value", values.get(i));
			jArray.add(jObj);
		}
		return jArray;
	}
	/**
	 * 获取数据库信息
	 * @return
	 */
	@RequestMapping(value="/db",method={RequestMethod.POST})
	@ResponseBody
	public JSONArray dbInfo(HttpServletRequest request){
		JSONArray jArray = new JSONArray();
		List<String> keys = new ArrayList<String>();
		keys.add("数据库类型");
		keys.add("数据库服务器地址");
		keys.add("数据库服务器端口");
		keys.add("数据库名称");
		keys.add("用户名");
		keys.add("连接池最大连接数");
		keys.add("后台显示SQL语句");
		List<String> values = new ArrayList<String>();
		PropertyPlaceholderConfigurerExt configurerExt = ApplicationContextHelper.getInstance().getBean("preferences");
		Properties prop = configurerExt.getHibernateProperties();
		String dbType = prop.getProperty("hibernate.dialect");
		String dbUrl = prop.getProperty("jdbc.master.url");
		String dbUser = prop.getProperty("jdbc.master.username");
		if(dbType.equals("org.hibernate.dialect.MySQL5Dialect") || dbType.equals("org.hibernate.dialect.MySQL5InnoDBDialect") || dbType.equals("org.hibernate.dialect.MySQLDialect") || dbType.equals("org.hibernate.dialect.MySQLLDialect") || dbType.equals("org.hibernate.dialect.MySQLLInnoDBDialect") || dbType.equals("org.hibernate.dialect.MySQLLMyISAMDialect")){
			dbType = "MySQL";
		}else if(dbType.equals("org.hibernate.dialect.Oracle10gDialect")){
			dbType = "Oracle10g";
		}else if(dbType.equals("org.hibernate.dialect.Oracle9iDialect")){
			dbType = "Oracle9i";
		}else if(dbType.equals("org.hibernate.dialect.Oracle9Dialect")){
			dbType = "Oracle9";
		}else if(dbType.equals("org.hibernate.dialect.Oracle8iDialect")){
			dbType = "Oracle8i";
		}else if(dbType.equals("org.hibernate.dialect.OracleDialect")){
			dbType = "Oracle";
		}else if(dbType.startsWith("SQLServer")){
			dbType = "SQLServer";
		}
		values.add(dbType);
		if(dbType.equals("MySQL")){
			String[] urlInfo = dbUrl.split("//");
			String[] dbUrlInfo = urlInfo[1].split(":");
			String[] dbNameInfo = dbUrlInfo[1].split("/");
			//链接地址 端口 数据库名
			values.add(dbUrlInfo[0]);//地址
			values.add(dbNameInfo[0]);//端口
			values.add(dbNameInfo[1]);//数据库名
		}else if(dbType.startsWith("Oracle")){
			String[] urlInfo = dbUrl.split("@");
			String[] dbUrlInfo = urlInfo[1].split(":");
			//链接地址 端口 数据库名
			values.add(dbUrlInfo[0]);//地址
			values.add(dbUrlInfo[1]);//端口
			values.add(dbUrlInfo[2]);//数据库名
		}else if(dbType.equals("SqlServer")){
			
		}
		//数据库用户名
		values.add(dbUser);
		values.add(prop.getProperty("jdbc.maxPoolSize"));
		values.add(prop.getProperty("hibernate.show_sql"));
		
		
		for(int i = 0;i<keys.size();i++){
			JSONObject jObj = new JSONObject();
			jObj.element("name", keys.get(i));
			jObj.element("value", values.get(i));
			jArray.add(jObj);
		}
		return jArray;
	}
	/**
	 * 获取操作系统及JDK信息
	 * @return
	 */
	@RequestMapping(value="/server",method={RequestMethod.POST})
	@ResponseBody
	public JSONArray serverInfo(HttpServletRequest request){
		JSONArray jArray = new JSONArray();
		List<String> keys = new ArrayList<String>();
		keys.add("操作系统");
		keys.add("操作系统版本");
		keys.add("操作系统补丁");
		keys.add("JDK厂商");
		keys.add("JDK版本");
		keys.add("JDK主目录");
		keys.add("Servlet容器");
		keys.add("JDK已用内存/最大可用数");
		keys.add("文件编码");
		
		List<String> values = new ArrayList<String>();
		Properties osProp = System.getProperties();
		
		values.add(osProp.getProperty("os.name"));
		values.add(osProp.getProperty("os.version"));
		values.add(osProp.getProperty("sun.os.patch.level"));
		values.add(osProp.getProperty("java.vendor"));
		values.add(osProp.getProperty("java.version"));
		values.add(osProp.getProperty("java.home"));
		values.add(request.getSession().getServletContext().getServerInfo());
		Runtime runtime = Runtime.getRuntime();
		long maxMemory = runtime.maxMemory();
		long freeMemory = runtime.freeMemory();
		values.add((maxMemory-freeMemory)/(1024*1024)+"M/"+(maxMemory/(1024*1024))+"M");
		values.add("UTF-8");
		
		for(int i = 0;i<keys.size();i++){
			JSONObject jObj = new JSONObject();
			jObj.element("name", keys.get(i));
			jObj.element("value", values.get(i));
			jArray.add(jObj);
		}
		return jArray;
	}
	/**
	 * 获取授权信息
	 * @return
	 */
	@RequestMapping(value="/license",method={RequestMethod.POST})
	@ResponseBody
	public JSONArray licenseInfo(){
		JSONArray jArray = new JSONArray();
		List<String> keys = new ArrayList<String>();
		keys.add("授权给");
		keys.add("有效期至");
		keys.add("授权用户数");
		keys.add("授权产品代码");
		keys.add("授权MAC地址");
		List<String> values = new ArrayList<String>();
		values.add("ZFRAME-Developer");
		values.add("2012-12-21 00:00:00");
		values.add("1");
		values.add("ZFRAME");
		values.add("3C-97-0E-07-0D-53");
		
		for(int i = 0;i<keys.size();i++){
			JSONObject jObj = new JSONObject();
			jObj.element("name", keys.get(i));
			jObj.element("value", values.get(i));
			jArray.add(jObj);
		}
		return jArray;
	}
}
