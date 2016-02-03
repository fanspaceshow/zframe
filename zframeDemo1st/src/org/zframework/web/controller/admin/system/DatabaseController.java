package org.zframework.web.controller.admin.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.aspectj.util.FileUtil;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.support.http.ContentType;
import org.zframework.core.util.Base64Util;
import org.zframework.core.util.DecUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.util.ZipUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.PageBean;
import org.zframework.orm.support.DBType;
import org.zframework.web.controller.BaseController;
import org.zframework.web.service.admin.system.DatabaseService;
import org.zframework.web.service.admin.system.LogService;
import org.zframework.web.service.admin.system.UserService;

@Controller
@RequestMapping("/admin/database")
public class DatabaseController extends BaseController<Object>{
	@Autowired
	private LogService logService;
	@Autowired
	private DatabaseService dbService;
	@Autowired
	private UserService userService;
	
	@RequestMapping(method={RequestMethod.GET})
	public String index(){
		logService.recordInfo("查询数据库管理","成功",getCurrentUser().getLoginName() , getRequestAddr());
		return "admin/system/database/index";
	}
	
	@RequestMapping(value="/sql",method={RequestMethod.POST})
	@ResponseBody
	public List<JSONObject> sqlResult(String sql){
		List<JSONObject> lstJson = new ArrayList<JSONObject>();
		sql = Base64Util.decode(sql).toLowerCase();
		if(sql.startsWith("select")){
			try {
				JSONObject jObj = new JSONObject();
				//判断数据量是否大于50，如果大于50，则要求分页
				if(dbService.queryCount("select count(*) from ("+sql+") c")>50){
					jObj.element("type", "size");
					lstJson.add(jObj);
					return lstJson;
				}
				List<?> lstResult = dbService.querySql(sql);
				
				jObj.element("type", "query");
				if(lstResult.size()==0){
					jObj.element("type", "empty");
					lstJson.add(jObj);
					return lstJson;
				}
				//获取查询结果的所有字段信息
				JSONArray jCols = new JSONArray();
				@SuppressWarnings("unchecked")
				Map<String,Object> firstRecord = (Map<String, Object>) lstResult.get(0);
				Iterator<String> iter = firstRecord.keySet().iterator();
				while(iter.hasNext()){
					String c = iter.next();
					if(c.toLowerCase().equals("id"))
						jCols.add(0, c);
					else
						jCols.add(c);
				}
				jObj.element("cols", jCols);
				List<JSONObject> lstData = new ArrayList<JSONObject>();
				
				for(Object res : lstResult){
					@SuppressWarnings("unchecked")
					Map<String,Object> record = (Map<String, Object>) res;
					
					JSONObject jData = new JSONObject();
					iter = record.keySet().iterator();
					while(iter.hasNext()){
						String key = iter.next();
						Object val = record.get(key);
						val = val==null?"":val;
						jData.element(key,val);
					}
					lstData.add(jData);
				}
				jObj.element("data", lstData);
				
				lstJson.add(jObj);
			} catch (Exception e) {
				//e.printStackTrace();
				JSONObject jObj = new JSONObject();
				jObj.element("type", "error");
				jObj.element("msg", e.getMessage());
				lstJson.add(jObj);
			}
		}else if(sql.indexOf("delete") < 0){
			JSONObject jObj = new JSONObject();
			try {
				jObj.element("id", 0);
				jObj.element("result", "受影响的行数:"+dbService.executeSql(sql));
				jObj.element("type", "notQuery");
			} catch (Exception e) {
				jObj.element("type", "error");
				jObj.element("msg", e.getMessage());
			}
			lstJson.add(jObj);
		}
		logService.recordInfo("数据库管理-执行SQL", "执行sql："+sql, getCurrentUser().getLoginName(), getRequestAddr());
		return lstJson;
	}
	@RequestMapping(value="/table/list",method={RequestMethod.POST})
	@ResponseBody
	public Map<String,Object> tableList(PageBean pageBean,String name,String value){
		Map<String,Object> map = new HashMap<String, Object>();
		List<?> lstTables = dbService.getTables(pageBean);
		List<JSONObject> lstJson = new ArrayList<JSONObject>();
		for(Object o : lstTables){
			JSONObject jObj = new JSONObject();
			Object[] objs = (Object[]) o;
			jObj.element("table_schema", objs[0]);
			jObj.element("table_name", objs[1]);
			jObj.element("create_time", DateFormat.getDateTimeInstance().format((Timestamp)objs[2]));
			lstJson.add(jObj);
		}
		map.put("rows", lstJson);
		map.put("total", pageBean.getTotalCount());
		return map;
	}
	/**
	 * 转向增加数据库表页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/addTable",method={RequestMethod.GET})
	public String addTable(Model model){
		return "admin/system/database/addTable";
	}
	/**
	 * 执行新增数据库表
	 * @param tableName
	 * @return
	 */
	@RequestMapping(value="/doAddTable",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doAddTable(String schema,String tableName,String entityName,String columns){
		if(!StringUtil.isEmpty(schema) && !StringUtil.isEmpty(tableName)&&!StringUtil.isEmpty(columns)){
			columns = "["+columns+"]";
			JSONArray jColumns = JSONArray.fromObject(columns);
			List<JSONObject> lstColumns = new ArrayList<JSONObject>();
			for(int i=0;i<jColumns.size();i++){
				lstColumns.add(jColumns.getJSONObject(i));
			}
			return dbService.executeCreateTable(schema.toUpperCase(),tableName.toUpperCase(),entityName,lstColumns);
		}else{
			return WebResult.error("数据不完整!");
		}
	}
	@RequestMapping(value="/doDropTable",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject doDropTable(String superpass,String[] tableNames){
		//return dbService.executeDropTable(tableNames);
		DecUtil des = new DecUtil();
		des.genKey(ApplicationCommon.DEC_KEY);// 生成密匙
		superpass = des.getEncString(superpass);
		if(userService.count(Restrictions.eq("loginName", "superadmin"),Restrictions.eq("passWord", superpass))>0){
			return dbService.executeDropTable(tableNames);
		}else{
			return WebResult.error("超级管理员密码验证不通过!");
		}
		
	}
	@RequestMapping(value="/datatypes",method={RequestMethod.POST})
	@ResponseBody
	public List<JSONObject> datatypes(){
		List<JSONObject> lstJson = new ArrayList<JSONObject>();
		DBType dbType = dbService.getDBType();
		String[] types = {};
		if(dbType==DBType.Oracle){//数据库类型为oracle
			types = new String[]{"VARCHAR2","CHAR","NUMBER","INTEGER","DATE","LONG","NVARCHAR2","NCHAR","CLOB","NCLOB","BLOB","BFILE","FLOAT","UROWID","DECIMAL","INT","VARCHAR"};
		}else if(dbType==DBType.Mysql){
			types = new String[]{"TINYINT","BIT","BOOL","SMALLINT","INT","INTEGER","BIGINT","FLOAT","DOUBLE","REAL","DECIMAL","DEC","NUMERIC","CHAR","VARCHAR","TINYBLOB","TINYTEXT","BLOB","TEXT","MEDIUMBLOB","MEDIUMTEXT","LONGBLOB","LONGTEXT","DATETIME","DATE","TIMESTAMP","TIME","YEAR"};
		}
		for(String type : types){
			JSONObject jObj = new JSONObject();
			jObj.element("id", type);
			jObj.element("text", type);
			lstJson.add(jObj);
		}
		return lstJson;
	}
	@RequestMapping(value="/schemas",method={RequestMethod.POST})
	@ResponseBody
	public List<JSONObject> schemas(){
		List<JSONObject> lstJson = new ArrayList<JSONObject>();
		List<?> lstSchemas = dbService.getSchemas();
		for(Object schema : lstSchemas){
			JSONObject jObj = new JSONObject();
			jObj.element("id", schema.toString());
			jObj.element("text", schema.toString());
			lstJson.add(jObj);
		}
		lstJson.get(0).element("selected", true);
		return lstJson;
	}
	/**
	 * 转向映射实体页面
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/refEntity/{tableName}",method={RequestMethod.GET})
	public String refEntity(Model model,@PathVariable("tableName") String tableName){
		//生成实体类代码
		String code = dbService.refEntity(tableName);
		model.addAttribute("code", code);
		model.addAttribute("tableName", tableName);
		return "admin/system/database/refEntity";
	}
	/**
	 * 生成Controller、service、index、add、edit页面
	 * @param 数据库表名
	 */
	@RequestMapping(value="/gen/{tableName}",method={RequestMethod.POST})
	@ResponseBody
	public JSONObject gender(HttpServletRequest request,@PathVariable("tableName") String tableName){
		String entityName = tableName;
		String singleEntityName = "";
		if(tableName.indexOf("_")>0){
			entityName = tableName.substring(tableName.indexOf("_")+1);
		}
		singleEntityName = entityName;
		entityName = entityName.substring(0,1).toUpperCase()+entityName.substring(1);
		
		//保存路径、模板路径并判断是否存在
		String savePath = request.getSession().getServletContext().getRealPath("/resources/reverse engine/"+entityName+"/views");
		File fSavePath = new File(savePath);
		fSavePath.mkdirs();
		savePath = request.getSession().getServletContext().getRealPath("/resources/reverse engine/"+entityName);
		fSavePath = new File(savePath);
		if(fSavePath.exists())//清理之前的文件
			fSavePath.delete();
		
		String templateDir = request.getSession().getServletContext().getRealPath("/WEB-INF/tpl/globals/template");
		File fTemplateDir = new File(templateDir);
		if(!fTemplateDir.exists())
			return WebResult.error("模板不存在!");
		
		//生成实体类
		try {
			String path = savePath+"/"+entityName+".java";
			File file = new File(path);
			if(file.exists())
				file.delete();
			file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(dbService.refEntity(tableName));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return WebResult.error("实体类生成失败!");
		}
		//生成Controller类、Service类
		String[] clazzes = new String[]{"Controller","Service"};
		for(String clazz : clazzes){
			try {
				StringBuffer sbContent = new StringBuffer();
				String template = templateDir+"/"+clazz+".tmp";
				File fTemplate = new File(template);
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fTemplate),"UTF-8"));
				while(br.ready()){
					String line = br.readLine();
					line = line.replaceAll("#EntityName", entityName).replaceAll("#SingleEntityName", singleEntityName);
					sbContent.append(line+"\r");
				}
				br.close();
				String path = savePath+"/"+entityName+clazz+".java";
				File file = new File(path);
				if(file.exists())
					file.delete();
				file.createNewFile();
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
				bw.write(sbContent.toString());
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				return WebResult.error(clazz+"类生成失败!");
			}
		}
		//生成index add edit页面
		String[] html = new String[]{"index","add","edit"};
		for(String h : html){
			try {
				StringBuffer sbContent = new StringBuffer();
				String template = templateDir+"/"+h+".tmp";
				File fTemplate = new File(template);
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fTemplate),"UTF-8"));
				while(br.ready()){
					String line = br.readLine();
					line = line.replaceAll("#EntityName", entityName).replaceAll("#SingleEntityName", singleEntityName);
					sbContent.append(line+"\r");
				}
				br.close();
				String path = savePath+"/views/"+h+".html";
				File file = new File(path);
				if(file.exists())
					file.delete();
				file.createNewFile();
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
				bw.write(sbContent.toString());
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				return WebResult.error(h+"页面生成失败!");
			}
		}
		
		//打包并删除文件夹
		ZipUtil.CompressFile(savePath, savePath+".zip");
		FileUtil.deleteContents(fSavePath);//删除目录下所有文件
		fSavePath.delete();//删除空文件夹
		return WebResult.success();
	}
	@RequestMapping(value="/dl/{tableName}",method={RequestMethod.POST})
	public void download(HttpServletRequest request,HttpServletResponse response,@PathVariable("tableName") String tableName){
		String entityName = tableName;
		if(tableName.indexOf("_")>0){
			entityName = tableName.substring(tableName.indexOf("_")+1);
		}
		entityName = entityName.substring(0,1).toUpperCase()+entityName.substring(1);
		String savePath = request.getSession().getServletContext().getRealPath("/resources/reverse engine/"+entityName);
		File file = new File(savePath+".zip");
		fileDownLoad(request, response, ContentType.ZIP, file);
	}
	@RequestMapping(value="/testCombo",method={RequestMethod.POST})
	@ResponseBody
	public List<JSONObject> testCombo(String val){
		List<JSONObject> lstJson = new ArrayList<JSONObject>();
		for(int i=0;i<10;i++){
			JSONObject jOpt = new JSONObject();
			jOpt.element("value", val+i);
			jOpt.element("text", val+i);
			
			lstJson.add(jOpt);
		}
		return lstJson;
	}
}
