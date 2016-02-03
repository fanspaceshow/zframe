package org.zframework.web.service.admin.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.ReflectUtil;
import org.zframework.core.util.RegexUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.core.web.support.WebResult;
import org.zframework.orm.dao.BaseHibernateDao;
import org.zframework.web.controller.BaseController;
import org.zframework.web.entity.system.Unit;
import org.zframework.web.entity.system.UnitType;
import org.zframework.web.entity.system.User;

import org.zframework.web.service.BaseService;

/**
 * 机构管理模块
 * 
 * @author ZENGCHAO
 * @time 2012-12-11 下午3:03:51
 * */
@Service
public class UnitService extends BaseService<Unit> {
	@Autowired
	private BaseHibernateDao baseDao;
	@Autowired
	private UnitTypeService unitTypeService;
	private BaseController<Unit> bc = new BaseController<Unit>();
	@Autowired
	private LogService logService;
	/***
	 * 根据机构编码查询机构是否存在
	 * 
	 * @param code
	 *            机构编码
	 * */
	public Unit getUnitByCode(String code) {
		return baseDao.getBy(Unit.class, Restrictions.eq("code", code));
	}

	/**
	 * 根据Id获取机构，并立即加载所关联的unitType对象
	 * 
	 * @param id
	 * */
	public Unit getUnit(int id) {
		 
		Unit unit=baseDao.get(Unit.class, id);
		 
		Hibernate.initialize(unit.getUnitType());//强制加载
	
		return unit;

	}
	/***
	 *获取机构列表信息
	 * @param id 传入的机构Id
	 * */
	public Map<String, Object> getUnitList(String name, String value,User user) {
		Map<String,Object> resMap = new HashMap<String, Object>();
		
		List<Criterion> criterionList = new ArrayList<Criterion>();//创建查询Criterion对象
		if(bc.isSuperadmin()==false){
			Unit userUnit=(Unit) user.getUnits().get(0);//获取当前登录用户的unitID
			criterionList.add(Restrictions.like("code", userUnit.getCode()+"%"));//添加查询条件
		}if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value)){
					criterionList.add(Restrictions.idEq(Integer.parseInt(value)));
				}
			}else{
				criterionList.add(Restrictions.like(name, "%"+value+"%"));
			}
		}
		List<Unit> unitList = this.list(criterionList.toArray(new Criterion[]{}),Order.asc("id"));
		//判断是否是超级管理员，超级管理员可以显示所有的单位，非超级管理员只能看到自己所属单位和下级单位
		if(!user.getLoginName().equals(ApplicationCommon.SYSTEM_ADMIN)){
			List<Unit> childList = new ArrayList<Unit>();
			getAllChildUnit(unitList,childList, user.getUnits().get(0).getId());
			childList.add(getUnitByIdInList(unitList,user.getUnits().get(0).getId()));
			unitList.clear();
			unitList.addAll(childList);
		}
		//ReflectUtil.removeLazyProperty(unitList);//移除延迟加载数据
		List<JSONObject> jResList = new ArrayList<JSONObject>();
		if(unitList!=null){
			for(Unit unit:unitList){
				ReflectUtil.removeLazyProperty(unit);
				JSONObject jRes = JSONObject.fromObject(unit);//转换json对象
				jRes.element("email", unit.getEMail());
				if(StringUtil.isEmpty(value)){//如果不存在查询条件，设置节点
					jRes.element("_parentId", unit.getParentId()==0?"":unit.getParentId());
				}else{
					if(bc.hasPropertyValueInArray(jResList,"id",unit.getParentId())){
						jRes.element("_parentId", unit.getParentId()==0?"":unit.getParentId());
					}
				}
				jRes.element("iconCls", "icon-unit");
				jResList.add(jRes);
			}
		}
		resMap.put("rows", jResList);
		return resMap;
	}
	private void getAllChildUnit(List<Unit>unitList,List<Unit> childList,int unitid){
		List<Unit> templist = new ArrayList<Unit>();
		for(Unit unit : unitList){
			if(unit.getParentId() == unitid){
				childList.add(unit);
				templist.add(unit);
			}
		}
		for(Unit unit : templist){
			getAllChildUnit(unitList,childList,unit.getId());
		}
	}
	private Unit getUnitByIdInList(List<Unit>unitList,int unitid){
		Unit unit = null;
		for(Unit u : unitList){
			if(u.getId() == unitid){
				unit = u;
				break;
			}
		}
		return unit;
	}
	/***
	 * 获取机构树
	 * @param parentId 父节点
	 * @param typeId 类型
	 * @param user 登录用户
	 * */
	public JSONArray getUnitTree(Integer parentId, Integer typeId,User user) {
		List<Criterion> criterionList = new ArrayList<Criterion>();//创建查询Criterion对象
		JSONArray jsonTree=new JSONArray();
		if(ObjectUtil.isNull(parentId)){//根据parentId的值构造一个节点
			parentId=0;
			JSONObject jNode=new JSONObject();
			jNode.element("id", 0);
			jNode.element("text", "无上级机构");
			jNode.element("code", "0000");
			jNode.element("iconCls", "icon-ok");
			jsonTree.add(jNode);
		}
		List<Unit> unitList=null;
		if(bc.isSuperadmin()==true){//如果是超级管理员查询全部
			UnitType u=new UnitType();
			u.setId(typeId);
			criterionList.add(Restrictions.eq("parentId", parentId));
			criterionList.add(Restrictions.eq("unitType", u));
			unitList=this.list(criterionList.toArray(new Criterion[]{}),Order.asc("id"));
		}else{//否则，根据 like code and parentId查询
			Unit userUnit=(Unit) user.getUnits().get(0);//获取当前登录用户的unitID
			criterionList.add(Restrictions.like("code", userUnit.getCode()+"%"));//添加查询条件
			criterionList.add(Restrictions.eq("parentId", parentId));
			criterionList.add(Restrictions.eq("unitType.id", typeId));
			unitList=this.list(criterionList.toArray(new Criterion[]{}),Order.asc("id"));
		}
		if(unitList.size()>0){
			for(Unit unit:unitList){//绑定查询的对象到控件中
				JSONObject jNode=new JSONObject();
				jNode.element("id", unit.getId());
				jNode.element("text", unit.getName());
				jNode.element("code", unit.getCode());
				if(this.count(Restrictions.eq("parentId", unit.getId()))>0)
					jNode.element("state", "closed");
				jNode.element("iconCls", "icon-unit");
				jsonTree.add(jNode);
			}
		}
		return jsonTree;
	}
	/**
	 * 获取机构类型
	 * */
	public JSONArray getUnitTypeTree() {
		List<Criterion> criterionList = new ArrayList<Criterion>();//创建查询Criterion对象
		JSONArray jsonTree=new JSONArray();
		 List<UnitType> typeList=unitTypeService.list(criterionList.toArray(new Criterion[]{}),Order.asc("id"));
		 
		if(ObjectUtil.isNotNull(typeList)){
			for(UnitType unitType:typeList){//绑定查询的对象到控件中
				JSONObject jNode=new JSONObject();
				jNode.element("id", unitType.getId());
				jNode.element("text", unitType.getName());
				jsonTree.add(jNode);
			}
		}
		return jsonTree;
	}
	/***
	 * 执行增加机构的操作
	 * */
	public JSONObject executeUnitAdd(HttpServletRequest request, Unit unit,User user) {
		Unit unitByName =this.get(Restrictions.eq("name", unit.getName()),Restrictions.eq("parentId", unit.getParentId()));
		if(ObjectUtil.isNotNull(unitByName)){//获取新增的机构名称，如果已经存在，则提示
			logService.recordInfo("新增机构","失败(机构名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("此机构名称已经存在!");
		}
		Unit unitByCode =this.getUnitByCode(unit.getCode());
		if(ObjectUtil.isNotNull(unitByCode)){//获取新增的机构编码，如果已经存在，则提示
			logService.recordInfo("新增机构","失败(机构编码已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("此机构编码已经存在!");
		}else{
			this.save(unit);
			logService.recordInfo("新增机构","成功", user.getLoginName(), request.getRemoteAddr());
			return WebResult.success();
		}
	}
	/**
	 * 转到修改页面
	 * */
	public String toUpdate(Model model, Integer id) {
		Unit unit =this.getUnit(id);
		Unit parentUnit=this.getById(unit.getParentId());
		if(!ObjectUtil.isNull(unit)){
			model.addAttribute("unit",unit);
			model.addAttribute("parentUnitId", parentUnit==null?0:parentUnit.getId());
			if(unit.getParentId()==0){
				model.addAttribute("parentUnitName", "无上级资源");
			}
			return "admin/system/unit/edit";
		}else{
			return ControllerCommon.UNAUTHORIZED_ACCESS;
		}
	}
	/***
	 * 确认修改
	 * */
	public JSONObject executeEdit(HttpServletRequest request, Unit unit,
			BindingResult result,User user) {
		if(result.hasErrors()){
			StringBuffer errMsg = new StringBuffer();
			for(ObjectError oe : result.getAllErrors()){
				errMsg.append(oe.getDefaultMessage()+",");
			}
			logService.recordInfo("编辑机构","失败（未按要求填写表单）"+errMsg.toString(), user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("请按要求填写表单");
		}else{
			Unit oldUnit =this.getUnit(unit.getId());
			if(ObjectUtil.isNotNull(oldUnit)){
				if(ObjectUtil.equalProperty(unit, oldUnit, new String[]{"id","unitType","name","code","address","eMail","web","parentId"})){
					return WebResult.NoChange();
				}else{
					Unit eqUnit = this.get(Restrictions.eq("name", unit.getName()),Restrictions.eq("parentId", oldUnit.getParentId()),Restrictions.not(Restrictions.eq("id", unit.getId())));
					if(ObjectUtil.isNotNull(eqUnit)){
						logService.recordInfo("编辑机构","失败（机构名称重复）", user.getLoginName(), request.getRemoteAddr());
						return WebResult.error("此机构名称已经存在");
					}else if(unit.getParentId()==unit.getId()){
						logService.recordInfo("编辑机构","失败（目录选择不合法）", user.getLoginName(), request.getRemoteAddr());
						return WebResult.error("目录选择不合法");
					}else{
						oldUnit.setAddress(unit.getAddress());
						oldUnit.setCode(unit.getCode());
						oldUnit.setEMail(unit.getEMail());
						oldUnit.setName(unit.getName());
						oldUnit.setParentId(unit.getParentId());
						oldUnit.setUnitType(unit.getUnitType());
						oldUnit.setWeb(unit.getWeb());
						update(oldUnit);
						logService.recordInfo("编辑机构","成功", user.getLoginName(), request.getRemoteAddr());
						return WebResult.success();
					}
				}
			}else{
				logService.recordInfo("编辑机构","失败（尝试编辑不存在的机构）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error(ControllerCommon.UNAUTHORIZED_ACCESS);
			}
		}
	}
	/**
	 * 删除
	 * */
	public JSONObject executeDelete(HttpServletRequest request, Integer[] ids,User user) {
		this.delete(ids);
		//删除下级
		for(Integer pid : ids){
			List<Unit> list = this.getAllChildUnitList(pid);
			for(Unit unit : list)
				this.delete(unit);
		}
		logService.recordInfo("删除机构", "成功",user.getLoginName(), request.getRemoteAddr());
		return WebResult.success();
	}
	/**
	 * 提供给用户模块的方法
	 * */
	public JSONArray toUnitListForUser(Integer id) {
		if(ObjectUtil.isNull(id)){
			List<Unit> unitList = this.list(Restrictions.eq("parentId", 0),Order.asc("id"));
			//判断是否是超级管理员，超级管理员可以显示所有的单位，非超级管理员只能看到自己所属单位和下级单位
			if(!bc.getCurrentUser().getLoginName().equals(ApplicationCommon.SYSTEM_ADMIN)){
				List<Unit> childList = new ArrayList<Unit>();
				getAllChildUnit(unitList,childList, bc.getCurrentUser().getUnits().get(0).getId());
				childList.add(getUnitByIdInList(unitList,bc.getCurrentUser().getUnits().get(0).getId()));
				unitList.clear();
				unitList.addAll(childList);
			}
			//ReflectUtil.removeLazyProperty(unitList);//移除延迟加载数据
			JSONArray jsonTree=new JSONArray();
			if(unitList!=null){
				for(Unit unit:unitList){
					JSONObject jRes = new JSONObject();//转换json对象
					jRes.element("id", unit.getId());
					jRes.element("text", unit.getName());
					jRes.element("iconCls", "icon-unit");
					List<Unit> list=this.list(Restrictions.eq("parentId", unit.getId()));
					if(list.size()!=0){
						jRes.element("state", "open");
						JSONArray childList=new JSONArray();
						for(Unit childrenUnit:list){
							JSONObject childUnit=new JSONObject();
							childUnit.element("id", childrenUnit.getId());
							childUnit.element("text", childrenUnit.getName());
							childUnit.element("iconCls", "icon-unit");
							int count = this.count(Restrictions.eq("parentId", childrenUnit.getId()));
							if(count>0)
								childUnit.element("state", "closed");
							childList.add(childUnit);
						}
						jRes.element("children", childList);
					}
					jsonTree.add(jRes);
				}
			}
			return jsonTree;	
		}else{
			return toForUserByPId(id);
		}
	}
	
	/**
	 * 根据父级机构获取子集机构
	 * @param parentId
	 * @return
	 */
	public JSONArray toForUserByPId(Integer parentId) {
		List<Unit> unitList = this.list(Restrictions.eq("parentId", parentId),Order.asc("id"));
		JSONArray jsonTree=new JSONArray();
		if(unitList!=null){
			for(Unit unit:unitList){
				JSONObject jRes = new JSONObject();//转换json对象
				jRes.element("id", unit.getId());
				jRes.element("text", unit.getName());
				jRes.element("iconCls", "icon-unit");
				jsonTree.add(jRes);
			}
		}
		return jsonTree;
	}
	/**
	 * 获取机构信息
	 * 强制加载机构下的用户列表
	 * @param unitId
	 * @return
	 */
	public Unit getById_NoLazyUser(Integer unitId){
		Unit unit = getById(unitId);
		if(ObjectUtil.isNotNull(unit)){
			Hibernate.initialize(unit.getUsers());
		}
		return unit;
	}
	/**
	 * 获取父节点下所有子节点
	 * 不包含父机构本身
	 * @return
	 */
	public List<Unit> getAllChildUnitList(int parentId){
		List<Unit> unitList = this.list(Order.asc("id"));
		//判断是否是超级管理员，超级管理员可以显示所有的单位，非超级管理员只能看到自己所属单位和下级单位
		List<Unit> childList = new ArrayList<Unit>();
		getAllChildUnit(unitList,childList, parentId);
		//childList.add(getUnitByIdInList(unitList,parentId));
		unitList.clear();
		unitList.addAll(childList);
		return unitList;
	}
	/**
	 * 获取父节点下所有子节点
	 * 包含父机构本身
	 * @return
	 */
	public List<Unit> getAllChildUnitListHasSelf(int parentId){
		List<Unit> unitList = this.list(Order.asc("id"));
		//判断是否是超级管理员，超级管理员可以显示所有的单位，非超级管理员只能看到自己所属单位和下级单位
		List<Unit> childList = new ArrayList<Unit>();
		getAllChildUnit(unitList,childList, parentId);
		childList.add(getUnitByIdInList(unitList,parentId));
		unitList.clear();
		unitList.addAll(childList);
		return unitList;
	}
	/**
	 * 获取父节点下所有子节点
	 * 不包含父机构本身
	 * @return
	 */
	public List<Unit> getAllChildUnitList_NoLazyUser(int parentId){
		List<Unit> unitList = this.list(Order.asc("id"));
		//判断是否是超级管理员，超级管理员可以显示所有的单位，非超级管理员只能看到自己所属单位和下级单位
		List<Unit> childList = new ArrayList<Unit>();
		getAllChildUnit(unitList,childList, parentId);
		//childList.add(getUnitByIdInList(unitList,parentId));
		unitList.clear();
		unitList.addAll(childList);
		for(Unit unit : unitList){
			Hibernate.initialize(unit.getUsers());
		}
		return unitList;
	}
	/**
	 * 获取父节点下所有子节点
	 * 包含父机构本身
	 * @return
	 */
	public List<Unit> getAllChildUnitListHasSelf_NoLazyUser(int parentId){
		List<Unit> unitList = this.list(Order.asc("id"));
		//判断是否是超级管理员，超级管理员可以显示所有的单位，非超级管理员只能看到自己所属单位和下级单位
		List<Unit> childList = new ArrayList<Unit>();
		getAllChildUnit(unitList,childList, parentId);
		childList.add(getUnitByIdInList(unitList,parentId));
		unitList.clear();
		unitList.addAll(childList);
		for(Unit unit : unitList){
			Hibernate.initialize(unit.getUsers());
		}
		return unitList;
	}
}
