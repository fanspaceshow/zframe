package org.zframework.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Json工具类<br>
 * 
 */
public class JSONUtil {

	private static Log log = LogFactory.getLog(JSONUtil.class);

	/**
	 * 将不含日期时间格式的Java对象系列化为Json资料格式
	 * 
	 * @param pObject
	 *            传入的Java对象
	 * @return
	 */
	public static final String encodeObject2Json(Object pObject) {
		String jsonString = "[]";
		if (ObjectUtil.isNull(pObject)) {
			// log.warn("传入的Java对象为空,不能将其序列化为Json资料格式.请检查!");
		} else {
			if (pObject instanceof ArrayList<?>) {
				JSONArray jsonArray = JSONArray.fromObject(pObject);
				jsonString = jsonArray.toString();
			} else {
				JSONObject jsonObject = JSONObject.fromObject(pObject);
				jsonString = jsonObject.toString();
			}
		}
		if (log.isInfoEnabled()) {
			log.info("序列化后的JSON资料输出:\n" + jsonString);
		}
		return jsonString;
	}

	/**
	 * 将含有日期时间格式的Java对象系列化为Json资料格式<br>
	 * Json-Lib在处理日期时间格式是需要实现其JsonValueProcessor接口,所以在这里提供一个重载的方法对含有<br>
	 * 日期时间格式的Java对象进行序列化
	 * 
	 * @param pObject
	 *            传入的Java对象
	 * @return
	 */
	public static final String encodeObject2Json(Object pObject, String pFormatString) {
		String jsonString = "[]";
		if (ObjectUtil.isNull(pObject)) {
			// log.warn("传入的Java对象为空,不能将其序列化为Json资料格式.请检查!");
		} else {
			JsonConfig cfg = new JsonConfig();
			cfg.registerJsonValueProcessor(java.sql.Timestamp.class, new JSONValueProcessorImpl(pFormatString));
			cfg.registerJsonValueProcessor(java.util.Date.class, new JSONValueProcessorImpl(pFormatString));
			cfg.registerJsonValueProcessor(java.sql.Date.class, new JSONValueProcessorImpl(pFormatString));
			if (pObject instanceof ArrayList<?>) {
				JSONArray jsonArray = JSONArray.fromObject(pObject, cfg);
				jsonString = jsonArray.toString();
			} else {
				JSONObject jsonObject = JSONObject.fromObject(pObject, cfg);
				jsonString = jsonObject.toString();
			}
		}
		if (log.isInfoEnabled()) {
			log.info("序列化后的JSON资料输出:\n" + jsonString);
		}
		return jsonString;
	}

	/**
	 * 将分页信息压入JSON字符串
	 * 此类内部使用,不对外暴露
	 * @param JSON字符串
	 * @param totalCount
	 * @return 返回合并后的字符串
	 */
	public static String encodeJson2PageJson(String jsonString, Integer totalCount) {
		jsonString = "{TOTALCOUNT:" + totalCount + ", ROOT:" + jsonString + "}";
		if (log.isInfoEnabled()) {
			log.info("合并后的JSON资料输出:\n" + jsonString);
		}
		return jsonString;
	}

	/**
	 * 直接将List转为分页所需要的Json资料格式
	 * 
	 * @param list
	 *            需要编码的List对象
	 * @param totalCount
	 *            记录总数
	 * @param pDataFormat
	 *            时间日期格式化,传null则表明List不包含日期时间属性
	 */
	public static final String encodeList2PageJson(List<?> list, Integer totalCount, String dataFormat) {
		String subJsonString = "";
		if (ObjectUtil.isNull(dataFormat)) {
			subJsonString = encodeObject2Json(list);
		} else {
			subJsonString = encodeObject2Json(list, dataFormat);
		}
		String jsonString = "{TOTALCOUNT:" + totalCount + ", ROOT:" + subJsonString + "}";
		return jsonString;
	}
	/**
	 * Java对象转化成JSON对象
	 * @param obj
	 * @param 要排除的字段名称数组
	 * @return
	 */
	public static JSONObject toJsonObject(Object obj,String...fields){
		JSONObject jObj = new JSONObject();
		Field[] oFields = obj.getClass().getDeclaredFields();
		for(Field field : oFields){
			if("serialVersionUID".equals(field.getName()))
				continue;
			if(StringUtil.hasStr(fields, field.getName())){
				continue;
			}
			Object value = ReflectUtil.getFieldValue(obj, field.getName());
			//移除延迟加载属性
			ReflectUtil.removeLazyProperty(value);
			if(ObjectUtil.isNull(value)){
				jObj.element(field.getName(), "");
			}else{
				jObj.element(field.getName(), value);
			}
		}
		return jObj;
	}
	/**
	 * Java对象转化成JSON对象
	 * 不包含延迟加载属性
	 * @param obj
	 * @param fields
	 * @return
	 */
	public static JSONObject toJsonObjectNoLazy(Object obj,String...fields){
		JSONObject jObj = new JSONObject();
		Field[] oFields = obj.getClass().getDeclaredFields();
		for(Field field : oFields){
			if("serialVersionUID".equals(field.getName()))
				continue;
			if(StringUtil.hasStr(fields, field.getName())){
				continue;
			}
			Annotation a = field.getAnnotation(ManyToOne.class);
			if (a != null)
				continue;
			else{
				a = field.getAnnotation(OneToMany.class);
				if (a == null){
					a = field.getAnnotation(ManyToMany.class);
					if (a != null) {
						continue;
					}
				}
				else
					continue;
			}
			Object value = ReflectUtil.getFieldValue(obj, field.getName());
			//移除延迟加载属性
			//ReflectUtil.removeLazyProperty(value);
			if(ObjectUtil.isNull(value)){
				jObj.element(field.getName(), "");
			}else{
				jObj.element(field.getName(), value);
			}
		}
		return jObj;
	}
	/**
	 * Java集合转化成json数组
	 * @param list
	 * @param fields
	 * @return
	 */
	public static List<JSONObject> toJsonObjectList(List<Object> list,String...fields){
		List<JSONObject> jList = new ArrayList<JSONObject>();
		for(Object obj : list){
			jList.add(toJsonObject(obj, fields));
		}
		return jList;
	}
	/**
	 * Java集合转化成json数组
	 * 不包含延迟加载属性
	 * @param list
	 * @param fields
	 * @return
	 */
	public static List<JSONObject> toJsonObjectListNoLazy(List<?> list,String...fields){
		List<JSONObject> jList = new ArrayList<JSONObject>();
		for(Object obj : list){
			jList.add(toJsonObjectNoLazy(obj, fields));
		}
		return jList;
	}
}
