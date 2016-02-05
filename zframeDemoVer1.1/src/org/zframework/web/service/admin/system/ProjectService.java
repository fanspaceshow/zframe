package org.zframework.web.service.admin.system;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

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
import org.zframework.web.entity.system.Project;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;


@Service
public class ProjectService extends BaseService<Project> {
	@Autowired
	private LogService logService;
	/**
	 * 初始化数据字典
	 * 将数据库中的数据项加载到系统内存中
	 */
	public void InitPros(){
		//获取数据库中所有的数据项
		List<Project> list = list();
		ApplicationCommon.SYSCOMMONS.clear();
		if(ObjectUtil.isNotNull(list)){
			for(Project pro : list){
				ApplicationCommon.SYSCOMMONS.put(pro.getProjectname(),pro.getProjectname() );
			}
		}
	}

	/**
	 * 分页显示project
	 * @param pageBean
	 * */
	public List<Project> getproList(PageBean pageBean, String name, String value) {
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value)){
					 
					pageBean.addCriterion(Restrictions.idEq(Integer.parseInt(value)));
				}
			}else{
				
				pageBean.addCriterion(Restrictions.like(name, value+"%"));
			}
		}
		List<Project> proList=this.listByPage(pageBean);
		return proList;
	}
	/***
	 * 执行增加
	 * @param request
	 * @param comm
	 * @param result
	 * @param user
	 * */
	public JSONObject executeAdd(HttpServletRequest request, Project pro,User user) {
		Project proByKey=this.getByProperties("projectname",pro.getProjectname());
		if(ObjectUtil.isNotNull(proByKey)){
			logService.recordInfo("新增数据字典","失败(标识名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("标识已经存在!");
		}else{
			save(pro);
			//更新缓存
			if(ObjectUtil.isNull(ApplicationCommon.SYSCOMMONS.get(pro.getProjectname()))){
				ApplicationCommon.SYSCOMMONS.put(pro.getProjectname(),pro.getCreateprotime());
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
		List<Project> list=this.getByIds(ids);
		for(int i=0;i<list.size();i++){
			Project pro=list.get(i);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(pro.getProjectname()))){
					ApplicationCommon.SYSCOMMONS.remove(pro.getProjectname());
				}
		
		}
		this.delete(ids);
		logService.recordInfo("删除数据库字典", "成功",user.getLoginName(), request.getRemoteAddr());
		return WebResult.success();
	
	}
	/**
	 * 根据id获取
	 * */
	public Project getPro(Integer id) {
		Project pro =this.getById(id);
		return pro;
	}
	/**
	 * 确认编辑
	 * */
	public JSONObject executeEdit(HttpServletRequest request, Project pro,User user) {
		Project oldpro =this.getById(pro.getId());
		Project probyKey =this.getByProperties("projectname", pro.getProjectname());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldpro)){
			//判断是否修改
			if(ObjectUtil.equalProperty(pro, oldpro, new String[]{"id","projectname","projecttype","createprotime","appointdays","proplaydays","proschedule","proparticipant"})){
				return WebResult.NoChange();
			}//判断标识是否存在
			else if(probyKey!=null&&probyKey.getId()!=pro.getId()&&probyKey.getProjectname().equals(pro.getProjectname())){
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("标识已经存在，请重新输入");
			}else{//修改
				oldpro.setProjectname(pro.getProjectname());
				oldpro.setAppointdays(pro.getAppointdays());
				oldpro.setCreateprotime(pro.getCreateprotime());
				oldpro.setProjecttype(pro.getProjecttype());
				oldpro.setProparticipant(pro.getProparticipant());
				oldpro.setProplaydays(pro.getProplaydays());
				oldpro.setProschedule(pro.getProschedule());
				update(oldpro);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(pro.getProjectname()))){
					ApplicationCommon.SYSCOMMONS.remove(pro.getProjectname());
					ApplicationCommon.SYSCOMMONS.put(pro.getProjectname(),pro.getProjectname());
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
