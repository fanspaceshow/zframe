package org.zframework.web.service.admin.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.json.JSONObject;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;
import org.zframework.core.factory.config.PropertyPlaceholderConfigurerExt;
import org.zframework.core.support.ApplicationContextHelper;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.PageBean;
import org.zframework.orm.support.DBType;
import org.zframework.web.service.BaseService;
@Service
public class DatabaseService extends BaseService<Object>{
	
	/**
	 * 执行增删改
	 * @param sql
	 * @return
	 */
	public int executeSql(String sql)throws Exception{
		int count = 0;
		Query query = getBaseDao().getSQLQuery(sql);
		count = query.executeUpdate();
		return count;
	}
	
	/**
	 * 执行查询
	 * @throws Exception 
	 */
	public List<?> querySql(String sql) throws Exception{
		try{
			Query query = getBaseDao().getSQLQuery(sql);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			
			return query.list();
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
	}
	/**
	 * 获取数量
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public int queryCount(String sql) throws Exception{
		try{
			Query query = getBaseDao().getSQLQuery(sql);
			
			return Integer.parseInt(query.uniqueResult().toString());
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
	}
	/**
	 * 获取当前连接数据库所包含的所有表
	 * @param pageBean 
	 * @return
	 */
	public List<?> getTables(PageBean pageBean) {
		List<?> lstTables = new ArrayList<Object>();
		String sql = "";
		DBType dbType = getDBType();
		
		if(dbType == DBType.Oracle){//数据库类型为oracle
			sql  = "select a.tablespace_name,a.table_name,b.created from user_tables a left join user_objects b on a.table_name = b.object_name";
		}else if(dbType == DBType.Mysql){//数据库类型为mysql
			PropertyPlaceholderConfigurerExt configurerExt = ApplicationContextHelper.getInstance().getBean("preferences");
			Properties prop = configurerExt.getHibernateProperties();
			String dbUrl = prop.getProperty("jdbc.master.url");
			String schema = dbUrl.substring(dbUrl.lastIndexOf("/")+1);
			if(schema.indexOf("?")>0)
				schema = schema.substring(0,schema.indexOf("?"));
			sql = "SELECT table_schema,table_name,create_time FROM  INFORMATION_SCHEMA.TABLES where table_schema='"+schema+"'";
		}
		if(sql!=""){
			Query query = baseDao.getSQLQuery(sql);
			if(pageBean.getPage()<=1)
				query.setFirstResult(0);
			else
				query.setFirstResult((pageBean.getPage()-1)*pageBean.getRows());
			query.setMaxResults(pageBean.getRows());
			lstTables = query.list();
			pageBean.setTotalCount(Integer.parseInt(baseDao.getSQLQuery("select count(*) from ("+sql+") c").uniqueResult().toString()));
		}
		return lstTables;
	}
	public List<?> getSchemas(){
		List<?> lstSchema = new ArrayList<Object>();
		String sql = "";
		DBType dbType = getDBType();
		if(dbType == DBType.Oracle){//数据库类型为oracle
			sql  = "select tablespace_name from user_tables group by tablespace_name";
		}else if(dbType == DBType.Mysql){
			PropertyPlaceholderConfigurerExt configurerExt = ApplicationContextHelper.getInstance().getBean("preferences");
			Properties prop = configurerExt.getHibernateProperties();
			String dbUrl = prop.getProperty("jdbc.master.url");
			String schema = dbUrl.substring(dbUrl.lastIndexOf("/")+1);
			if(schema.indexOf("?")>0)
				schema = schema.substring(0,schema.indexOf("?"));
			sql = "SELECT table_schema FROM  INFORMATION_SCHEMA.TABLES where table_schema='"+schema+"' group by table_schema";
		}
		if(sql!=""){
			Query query = baseDao.getSQLQuery(sql);
			lstSchema = query.list();
		}
		return lstSchema;
	}
	/**
	 * 执行创建表格
	 */
	public JSONObject executeCreateTable(String schema,String tableName,String entityName,List<JSONObject> columns) {
		DBType dbType = getDBType();
		if(dbType == DBType.Oracle){
			if(getBaseDao().countByNativeSQL("SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = '"+tableName+"'")>0){
				return WebResult.error(tableName+"表已存在!");
			}
			List<String> lstPrimary = new ArrayList<String>();
			List<String> lstUnique = new ArrayList<String>();
			StringBuffer sql = new StringBuffer("CREATE TABLE "+tableName+"(\n");
			String template = "{0}\t{1}({2} {3})\t {4},";
			String template2 = "{0}\t{1}\t {2},";
			for(JSONObject jObj : columns){
				if(jObj.getString("datatype").equals("NUMBER") || jObj.getString("datatype").equals("INTEGER") || jObj.getString("datatype").equals("LONG") || jObj.getString("datatype").equals("FLOAT") || jObj.getString("datatype").equals("INT"))
					sql.append(StringUtil.replaceRegex(template2, jObj.getString("column_name"),jObj.getString("datatype"),jObj.getString("notnull").equals("是")?"NOT NULL":"NULL"));
				else
					sql.append(StringUtil.replaceRegex(template, jObj.getString("column_name"),jObj.getString("datatype"),jObj.getString("len"),jObj.getString("b_c"),jObj.getString("notnull").equals("是")?"NOT NULL":"NULL"));
				if(jObj.getBoolean("primary")){
					lstPrimary.add(jObj.getString("column_name"));
				}
				if(jObj.getBoolean("unique")){
					lstUnique.add(jObj.getString("column_name"));
				}
			}
			sql = new StringBuffer(sql.substring(0,sql.length()-1));
			sql.append("\n)");
			sql.append("TABLESPACE \t"+schema);
			try {
				if(getBaseDao().getSQLQuery(sql.toString()).executeUpdate()>=0){
					if(lstPrimary.size()>0){
						String sPSql = StringUtil.replaceRegex("ALTER TABLE {0} ADD (PRIMARY KEY({1}) USING INDEX TABLESPACE {2})",tableName,StringUtil.toString(lstPrimary),schema);
						if(getBaseDao().getSQLQuery(sPSql).executeUpdate()<0){
							return WebResult.error("主键约束设置失败!");
						}
					}
					if(lstUnique.size()>0){
						String sUSql = StringUtil.replaceRegex("ALTER TABLE {0} ADD (UNIQUE({1}) USING INDEX TABLESPACE {2})",tableName,StringUtil.toString(lstUnique),schema);
						if(getBaseDao().getSQLQuery(sUSql).executeUpdate()<0){
							return WebResult.error("唯一约束设置失败!");
						}
					}
					//创建序列
					getBaseDao().getSQLQuery("CREATE SEQUENCE "+schema+".SEQ_"+tableName+" START WITH 1 MAXVALUE 999999999999999999999999999 MINVALUE 1 NOCYCLE CACHE 20  NOORDER").executeUpdate();
					return WebResult.success();
				}else{
					return WebResult.error("数据库表创建失败!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				return WebResult.error("数据库表创建失败("+e.getMessage()+")");
			}
			
		}else if(dbType == DBType.Mysql){
			if(getBaseDao().countByNativeSQL("SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = '"+tableName+"'")>0){
				return WebResult.error(tableName+"表已存在!");
			}
		}
		return WebResult.error("数据库表创建失败!");
	}
	public JSONObject executeDropTable(String[] tableNames){
		DBType dbType = getDBType();
		if(dbType == DBType.Oracle){
			for(String tableName : tableNames){
				getBaseDao().getSQLQuery("drop table "+tableName).executeUpdate();
				getBaseDao().getSQLQuery("DROP SEQUENCE SEQ_"+tableName).executeUpdate();
			}
			return WebResult.success();
		}else if(dbType == DBType.Mysql){
			for(String tableName : tableNames){
				getBaseDao().getSQLQuery("drop table "+tableName).executeUpdate();
			}
			return WebResult.success();
		}
		return WebResult.error("数据库表删除失败!");
	}
	/**
	 * 获取表结构
	 * @param tableName
	 * @return
	 */
	public List<Map<String,Object>> getTableStructure(String tableName){
		List<Map<String,Object>> structure = new ArrayList<Map<String,Object>>();
		DBType dbType = getDBType();
		if(dbType == DBType.Oracle){
			String sql = "select column_name,data_type,nullable from user_tab_columns where Table_Name='"+tableName+"'";
			List<Map<String,Object>> list = getBaseDao().queryForList(sql);
			for(Map<String,Object> map : list){
				String field = map.get("COLUMN_NAME").toString();
				boolean isPri = "ID".equals(field)?true:false;
				boolean isNotNull = "N".equals(map.get("NULLABLE"))?true:false;
				String type = map.get("DATA_TYPE").toString();
				
				Map<String,Object> mapRow = new HashMap<String, Object>();
				mapRow.put("field", field.toLowerCase());
				mapRow.put("type", type.toLowerCase());
				mapRow.put("primary", isPri);
				mapRow.put("isNotNull", isNotNull);
				
				structure.add(mapRow);
			}
		}else if(dbType == DBType.Mysql){
			String sql = "DESC "+tableName;
			List<Map<String,Object>> list = getBaseDao().queryForList(sql);
			for(Map<String,Object> map : list){
				String field = map.get("Field").toString();
				boolean isPri = "PRI".equals(map.get("Key"))?true:false;
				boolean isNotNull = "NO".equals(map.get("Null"))?true:false;
				String type = map.get("Type").toString();
				if(type.indexOf("(")>0)
					type = type.substring(0,type.indexOf("("));
				Map<String,Object> mapRow = new HashMap<String, Object>();
				mapRow.put("field", field);
				mapRow.put("type", type.toLowerCase());
				mapRow.put("primary", isPri);
				mapRow.put("isNotNull", isNotNull);
				
				structure.add(mapRow);
			}
		}
		return structure;
	}
	public String refEntity(String tableName) {
		String entityName = tableName;
		if(tableName.indexOf("_")>0)
			entityName = tableName.substring(tableName.indexOf("_")+1);
		entityName = entityName.substring(0,1).toUpperCase()+entityName.substring(1);
		
		StringBuffer code = new StringBuffer("package org.zframework.web.entity;\r\rimport java.io.Serializable;\rimport javax.persistence.Entity;\rimport javax.persistence.Table;\rimport javax.persistence.Id;\rimport javax.validation.constraints.NotNull;\rimport javax.persistence.Column;\rimport javax.persistence.GeneratedValue;\rimport javax.persistence.GenerationType;\rimport javax.persistence.SequenceGenerator;\r\r@Entity\r@Table(name=\""+tableName+"\")\rpublic class "+entityName+" implements Serializable{\r");
		List<Map<String,Object>> list = getTableStructure(tableName);//获取表结构
		StringBuffer codeGetSet = new StringBuffer("\r");
		for(Map<String,Object> map : list){
			String field = map.get("field").toString();
			boolean isPri = Boolean.parseBoolean(map.get("primary").toString());
			boolean isNotNull = Boolean.parseBoolean(map.get("isNotNull").toString());
			String type = map.get("type").toString();
			String colType = "int";
			if("int".equals(type) || "bigint".equals(type) || "number".equals(type) || "smallint".equals(type)|| "integer".equals(type)){
				colType = "int";
			}else if("double".equals(type)){
				colType = "double";
			}else if("float".equals(type)){
				colType = "float";
			}else if("decimal".equals(type)){
				colType = "decimal";
			}else if("bit".equals(type) || "bool".equals(type) || "boolean".equals(type)){
				colType = "boolean";
			}else if("clob".equals(type) ||"blob".equals(type) || "char".equals(type) || "date".equals(type) || "datetime".equals(type) || "longblob".equals(type) || "longtext".equals(type) || "text".equals(type) || "varchar".equals(type)|| "varchar2".equals(type)){
				colType = "String";
			}
			code.append("\r");
			if(isPri){
				code.append("\t@Id\r");
				code.append("\t@GeneratedValue(strategy=GenerationType.AUTO,generator=\"seq_"+tableName.toLowerCase()+"\")\r");
				code.append("\t@SequenceGenerator(name=\"seq_"+tableName.toLowerCase()+"\",sequenceName=\"seq_"+tableName.toLowerCase()+"\")\r");
			}else{
				code.append("\t@Column\r");
			}
			if(isNotNull){
				code.append("\t@NotNull\r");
			}
			code.append("\tpublic "+colType+" "+field+";\r");
			
			String pr = field.substring(0,1).toUpperCase();
			String suf = field.substring(1);
			codeGetSet.append("\tpublic "+colType+" get"+pr+suf+"() {\r");
			codeGetSet.append("\t\treturn "+field+";\r\t}\r\r");
			codeGetSet.append("\tpublic void set"+pr+suf+"("+colType+" "+field+") {\r");
			codeGetSet.append("\t\tthis."+field+"="+field+";\r\t}\r\r");
		}
		code.append(codeGetSet);
		code.append("\r}");
		return code.toString();
	}
}
