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
import org.zframework.web.entity.system.SupplierList;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
@Service
public class SupplierListService extends BaseService<SupplierList>  {
	@Autowired
	private LogService logService;
	Log log  = LogFactory.getLog("SupplierListService");
	/**
	 * 初始化数据字典
	 * 将数据库中的数据项加载到系统内存中
	 */
	public void InitPros(){
		//获取数据库中所有的数据项
		List<SupplierList> list = list();
		ApplicationCommon.SYSCOMMONS.clear();
		if(ObjectUtil.isNotNull(list)){
			for(SupplierList supplier : list){
				ApplicationCommon.SYSCOMMONS.put(supplier.getSuppliername(), supplier.getSuppliername());
			}
		}
}
	/**
	 * 分页显示SupplierList
	 * @param pageBean
	 * */
	public List<SupplierList> getproList(PageBean pageBean, String name, String value) {
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value)){
					 
					pageBean.addCriterion(Restrictions.idEq(Integer.parseInt(value)));
				}
			}else{
				
				pageBean.addCriterion(Restrictions.like(name, value+"%"));
			}
		}
		List<SupplierList> proList=this.listByPage(pageBean);
		return proList;
	}
	
	/***
	 * 执行增加
	 * @param request
	 * @param comm
	 * @param result
	 * @param user
	 * */
	public JSONObject executeAdd(HttpServletRequest request, SupplierList supplier,User user) {
		SupplierList supplierByKey=this.getByProperties("suppliername",supplier.getSuppliername());
		if(ObjectUtil.isNotNull(supplierByKey)){
			logService.recordInfo("新增数据字典","失败(标识名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("标识已经存在!");
		}else{
			save(supplier);
			//更新缓存
			if(ObjectUtil.isNull(ApplicationCommon.SYSCOMMONS.get(supplier.getSuppliername()))){
				ApplicationCommon.SYSCOMMONS.put(supplier.getSuppliername(),supplier.getSuppliername());
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
		List<SupplierList> list=this.getByIds(ids);
		for(int i=0;i<list.size();i++){
			SupplierList supplier=list.get(i);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(supplier.getSuppliername()))){
					ApplicationCommon.SYSCOMMONS.remove(supplier.getSuppliername());
				}		
		}
		this.delete(ids);
		logService.recordInfo("删除数据库字典", "成功",user.getLoginName(), request.getRemoteAddr());
		return WebResult.success();
	}
	
	
	/**
	 * 根据id获取
	 * */
	public SupplierList getPro(Integer id) {
		SupplierList supplier =this.getById(id);
		return supplier;
	}
	
	
	/**
	 * 确认编辑
	 * */
	public JSONObject executeEdit(HttpServletRequest request, SupplierList supplier,User user) {
		log.error("into SupplierList executeEdit");
		SupplierList oldsupplier =this.getById(supplier.getId());
		SupplierList supplierKey =this.getByProperties("suppliername", supplier.getSuppliername());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldsupplier)){
			//判断是否修改
			if(ObjectUtil.equalProperty(supplier, oldsupplier, new String[]{"id","suppliername","addr","personname","phone","email"})){
				return WebResult.NoChange();
			}//判断标识是否存在
			else if(supplierKey!=null&&supplierKey.getId()!=supplier.getId()&&supplierKey.getSuppliername().equals(supplier.getSuppliername())){
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("标识已经存在，请重新输入");
			}else{//修改
				oldsupplier.setSuppliername(supplier.getSuppliername());
				oldsupplier.setAddr(supplier.getAddr());			
				oldsupplier.setPersonname(supplier.getPersonname());
				oldsupplier.setPhone(supplier.getPhone());
				oldsupplier.setEmail(supplier.getEmail());
				update(oldsupplier);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(supplier.getSuppliername()))){
					ApplicationCommon.SYSCOMMONS.remove(supplier.getSuppliername());
					ApplicationCommon.SYSCOMMONS.put(supplier.getSuppliername(),supplier.getSuppliername());
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
	public  List<SupplierList> getSupplierlist(){
		//获取数据库中所有的数据项
		List<SupplierList> list = list();
		return list;
	}
}
