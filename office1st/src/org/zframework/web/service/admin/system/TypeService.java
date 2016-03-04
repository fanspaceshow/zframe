package org.zframework.web.service.admin.system;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.query.PageBean;

import org.zframework.web.entity.system.Measureunits;
import org.zframework.web.entity.system.Type;
import org.zframework.web.entity.system.User;

import org.zframework.web.service.BaseService;

@Service
public class TypeService extends BaseService<Type>{
	@Autowired
	private LogService logService;
	Log log  = LogFactory.getLog("TypeService");
	/**
	 * 初始化数据字典
	 * 将数据库中的数据项加载到系统内存中
	 */
	public void InitPros(){
		//获取数据库中所有的数据项
		List<Type> list = list();
		ApplicationCommon.SYSCOMMONS.clear();
		if(ObjectUtil.isNotNull(list)){
			for(Type type : list){
				ApplicationCommon.SYSCOMMONS.put(type.getType(),type.getType());
			}
		}
}
	/**
	 * 分页显示Warehouse
	 * @param pageBean
	 * */
	public List<Type> getproList(PageBean pageBean, String name, String value) {
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value)){
					 
					pageBean.addCriterion(Restrictions.idEq(Integer.parseInt(value)));
				}
			}else{
				
				pageBean.addCriterion(Restrictions.like(name, value+"%"));
			}
		}
		List<Type> proList=this.listByPage(pageBean);
		return proList;
	}
	
	/***
	 * 执行增加
	 * @param request
	 * @param comm
	 * @param result
	 * @param user
	 * */
	public JSONObject executeAdd(HttpServletRequest request, Type type,User user) {
		Type typeByKey=this.getByProperties("type",type.getType());
		if(ObjectUtil.isNotNull(typeByKey)){
			logService.recordInfo("新增数据字典","失败(标识名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("标识已经存在!");
		}else{
			save(type);
			//更新缓存
			if(ObjectUtil.isNull(ApplicationCommon.SYSCOMMONS.get(type.getType()))){
				ApplicationCommon.SYSCOMMONS.put(type.getType(),type.getType());
			}
			logService.recordInfo("新增数据字典","成功", user.getLoginName(), request.getRemoteAddr());
			return WebResult.success();
		}
	}
	
	
	/**
	 * 删除操作
	 * */
	public JSONObject executeDelete(HttpServletRequest request, Integer[] ids,
			JSONObject jResult,User user) {
		List<Type> list=this.getByIds(ids);
		for(int i=0;i<list.size();i++){
			Type type=list.get(i);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(type.getType()))){
					ApplicationCommon.SYSCOMMONS.remove(type.getType());
				}		
		}
		this.delete(ids);
		logService.recordInfo("删除数据库字典", "成功",user.getLoginName(), request.getRemoteAddr());
		return WebResult.success();
	}
	
	
	/**
	 * 根据id获取
	 * */
	public Type getPro(Integer id) {
		Type type =this.getById(id);
		return type;
	}
	
	
	/**
	 * 确认编辑
	 * */
	public JSONObject executeEdit(HttpServletRequest request, Type type,User user) {
		log.error("into typeservice executeEdit");
		Type oldtype =this.getById(type.getId());
		Type typeKey =this.getByProperties("type", type.getType());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldtype)){
			//判断是否修改
			if(ObjectUtil.equalProperty(type, oldtype, new String[]{"id","type"})){
				return WebResult.NoChange();
			}//判断标识是否存在
			else if(typeKey!=null&&typeKey.getId()!=type.getId()&&typeKey.getType().equals(type.getType())){
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("标识已经存在，请重新输入");
			}else{//修改
				oldtype.setType(type.getType());
				update(oldtype);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(type.getType()))){
					ApplicationCommon.SYSCOMMONS.remove(type.getType());
					ApplicationCommon.SYSCOMMONS.put(type.getType(),type.getType());
				}
				logService.recordInfo("编辑数据字典","成功", user.getLoginName(), request.getRemoteAddr());
				return WebResult.success();
			}
		}else{
			logService.recordInfo("编辑数据字典","失败（尝试编辑不存在的数据字典）", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error(ControllerCommon.UNAUTHORIZED_ACCESS);
		}
	}
	
	
	/**
	 * 获取项目类型的list
	 */
	public  List<Type> getTypes(){
		//获取数据库中所有的数据项
		List<Type> list = list();
		return list;
	}
}
