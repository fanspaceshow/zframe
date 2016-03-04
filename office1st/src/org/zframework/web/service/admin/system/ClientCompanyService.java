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

import org.zframework.web.entity.system.Client;


import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
@Service
public class ClientCompanyService extends BaseService<Client>  {
	@Autowired
	private LogService logService;
	Log log  = LogFactory.getLog("ClientCompanyService");
	/**
	 * 初始化数据字典
	 * 将数据库中的数据项加载到系统内存中
	 */
	public void InitPros(){
		//获取数据库中所有的数据项
		List<Client> list = list();
		ApplicationCommon.SYSCOMMONS.clear();
		if(ObjectUtil.isNotNull(list)){
			for(Client client : list){
				ApplicationCommon.SYSCOMMONS.put(client.getName(), client.getName());
			}
		}
}
	/**
	 * 分页显示clientcompany
	 * @param pageBean
	 * */
	public List<Client> getproList(PageBean pageBean, String name, String value) {
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value)){
					 
					pageBean.addCriterion(Restrictions.idEq(Integer.parseInt(value)));
				}
			}else{
				
				pageBean.addCriterion(Restrictions.like(name, value+"%"));
			}
		}
		List<Client> proList=this.listByPage(pageBean);
		return proList;
	}
	
	/***
	 * 执行增加
	 * @param request
	 * @param comm
	 * @param result
	 * @param user
	 * */
	public JSONObject executeAdd(HttpServletRequest request, Client pro,User user) {
		Client proByKey=this.getByProperties("name",pro.getName());
		if(ObjectUtil.isNotNull(proByKey)){
			logService.recordInfo("新增数据字典","失败(标识名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("标识已经存在!");
		}else{
			save(pro);
			//更新缓存
			if(ObjectUtil.isNull(ApplicationCommon.SYSCOMMONS.get(pro.getName()))){
				ApplicationCommon.SYSCOMMONS.put(pro.getName(),pro.getCompany());
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
		List<Client> list=this.getByIds(ids);
		for(int i=0;i<list.size();i++){
			Client pro=list.get(i);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(pro.getName()))){
					ApplicationCommon.SYSCOMMONS.remove(pro.getName());
				}		
		}
		this.delete(ids);
		logService.recordInfo("删除数据库字典", "成功",user.getLoginName(), request.getRemoteAddr());
		return WebResult.success();
	}
	
	
	/**
	 * 根据id获取
	 * */
	public Client getPro(Integer id) {
		Client client =this.getById(id);
		return client;
	}
	
	
	/**
	 * 确认编辑
	 * */
	public JSONObject executeEdit(HttpServletRequest request, Client client,User user) {
		log.error("into clientService executeEdit");
		Client oldclient =this.getById(client.getId());
		Client clientKey =this.getByProperties("name", client.getName());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldclient)){
			//判断是否修改
			if(ObjectUtil.equalProperty(client, oldclient, new String[]{"id","company","age"})){
				return WebResult.NoChange();
			}//判断标识是否存在
			else if(clientKey!=null&&clientKey.getId()!=client.getId()&&clientKey.getName().equals(client.getName())){
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("标识已经存在，请重新输入");
			}else{//修改
				oldclient.setName(client.getName());
				oldclient.setCompany(client.getCompany());			
				oldclient.setAge(client.getAge());				
				update(oldclient);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(client.getName()))){
					ApplicationCommon.SYSCOMMONS.remove(client.getName());
					ApplicationCommon.SYSCOMMONS.put(client.getName(),client.getName());
				}
				logService.recordInfo("编辑数据字典","成功", user.getLoginName(), request.getRemoteAddr());
				return WebResult.success();
			}
		}else{
			logService.recordInfo("编辑数据字典","失败（尝试编辑不存在的数据字典）", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error(ControllerCommon.UNAUTHORIZED_ACCESS);
		}
	} 
}
