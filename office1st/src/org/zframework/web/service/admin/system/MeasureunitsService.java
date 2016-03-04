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
import org.zframework.web.entity.system.User;

import org.zframework.web.service.BaseService;

@Service
public class MeasureunitsService extends BaseService<Measureunits>{
	@Autowired
	private LogService logService;
	Log log  = LogFactory.getLog("WarehouseService");
	/**
	 * 初始化数据字典
	 * 将数据库中的数据项加载到系统内存中
	 */
	public void InitPros(){
		//获取数据库中所有的数据项
		List<Measureunits> list = list();
		ApplicationCommon.SYSCOMMONS.clear();
		if(ObjectUtil.isNotNull(list)){
			for(Measureunits unit : list){
				ApplicationCommon.SYSCOMMONS.put(unit.getUnit(),unit.getUnit() );
			}
		}
}
	/**
	 * 分页显示Warehouse
	 * @param pageBean
	 * */
	public List<Measureunits> getproList(PageBean pageBean, String name, String value) {
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value)){
					 
					pageBean.addCriterion(Restrictions.idEq(Integer.parseInt(value)));
				}
			}else{
				
				pageBean.addCriterion(Restrictions.like(name, value+"%"));
			}
		}
		List<Measureunits> proList=this.listByPage(pageBean);
		return proList;
	}
	
	/***
	 * 执行增加
	 * @param request
	 * @param comm
	 * @param result
	 * @param user
	 * */
	public JSONObject executeAdd(HttpServletRequest request, Measureunits unit,User user) {
		Measureunits unitByKey=this.getByProperties("unit",unit.getUnit());
		if(ObjectUtil.isNotNull(unitByKey)){
			logService.recordInfo("新增数据字典","失败(标识名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("标识已经存在!");
		}else{
			save(unit);
			//更新缓存
			if(ObjectUtil.isNull(ApplicationCommon.SYSCOMMONS.get(unit.getUnit()))){
				ApplicationCommon.SYSCOMMONS.put(unit.getUnit(),unit.getUnit());
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
		List<Measureunits> list=this.getByIds(ids);
		for(int i=0;i<list.size();i++){
			Measureunits unit=list.get(i);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(unit.getUnit()))){
					ApplicationCommon.SYSCOMMONS.remove(unit.getUnit());
				}		
		}
		this.delete(ids);
		logService.recordInfo("删除数据库字典", "成功",user.getLoginName(), request.getRemoteAddr());
		return WebResult.success();
	}
	
	
	/**
	 * 根据id获取
	 * */
	public Measureunits getPro(Integer id) {
		Measureunits unit =this.getById(id);
		return unit;
	}
	
	
	/**
	 * 确认编辑
	 * */
	public JSONObject executeEdit(HttpServletRequest request, Measureunits unit,User user) {
		log.error("into MeasureunitsService executeEdit");
		Measureunits oldunit =this.getById(unit.getId());
		Measureunits unitKey =this.getByProperties("unit", unit.getUnit());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldunit)){
			//判断是否修改
			if(ObjectUtil.equalProperty(unit, oldunit, new String[]{"id","unit"})){
				return WebResult.NoChange();
			}//判断标识是否存在
			else if(unitKey!=null&&unitKey.getId()!=unit.getId()&&unitKey.getUnit().equals(unit.getUnit())){
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("标识已经存在，请重新输入");
			}else{//修改
				oldunit.setUnit(unit.getUnit());
				update(oldunit);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(unit.getUnit()))){
					ApplicationCommon.SYSCOMMONS.remove(unit.getUnit());
					ApplicationCommon.SYSCOMMONS.put(unit.getUnit(),unit.getUnit());
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
	public  List<Measureunits> getunits(){
		//获取数据库中所有的数据项
		List<Measureunits> list = list();
		return list;
	}
}
