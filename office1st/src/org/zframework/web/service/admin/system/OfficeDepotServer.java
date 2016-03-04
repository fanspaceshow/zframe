package org.zframework.web.service.admin.system;

import java.text.SimpleDateFormat;
import java.util.Date;
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

import org.zframework.web.entity.system.OfficeOut;
import org.zframework.web.entity.system.OfficeTable;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;

@Service
public class OfficeDepotServer extends BaseService<OfficeTable>{
	@Autowired
	private LogService logService;
	Log log  = LogFactory.getLog("OfficeDepotServer");
	
	/**
	 * 初始化数据字典
	 * 将数据库中的数据项加载到系统内存中
	 */
	public void InitPros(){
		//获取数据库中所有的数据项
		List<OfficeTable> list = list();
		ApplicationCommon.SYSCOMMONS.clear();
		if(ObjectUtil.isNotNull(list)){
			for(OfficeTable office : list){
				ApplicationCommon.SYSCOMMONS.put(office.getGoodsname(), office.getGoodsname());
			}
		}
}
	
	/**
	 * 分页显示OfficeTable
	 * @param pageBean
	 * */
	public List<OfficeTable> getproList(PageBean pageBean, String name, String value) {
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value)){
					 
					pageBean.addCriterion(Restrictions.idEq(Integer.parseInt(value)));
				}
			}else if ("warehouseamount".equals(name)) {
				pageBean.addCriterion(Restrictions.eq("warehouseamount", Integer.parseInt(value)));
			}else{
				
				pageBean.addCriterion(Restrictions.like(name, value+"%"));
			}
		}
		List<OfficeTable> proList=this.listByPage(pageBean);
		return proList;
	}
	
	
	
	/***
	 * 执行增加
	 * @param request
	 * @param comm
	 * @param result
	 * @param user
	 * */
	public JSONObject executeAdd(HttpServletRequest request, OfficeTable office,User user) {
		OfficeTable proByKey=this.getByProperties("goodsname",office.getGoodsname());
		if(ObjectUtil.isNotNull(proByKey)){
			logService.recordInfo("新增数据字典","失败(标识名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("标识已经存在!");
		}else{
			save(office);
			//更新缓存
			if(ObjectUtil.isNull(ApplicationCommon.SYSCOMMONS.get(office.getGoodsname()))){
				ApplicationCommon.SYSCOMMONS.put(office.getGoodsname(),office.getGoodsname());
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
		List<OfficeTable> list=this.getByIds(ids);
		for(int i=0;i<list.size();i++){
			OfficeTable office=list.get(i);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(office.getGoodsname()))){
					ApplicationCommon.SYSCOMMONS.remove(office.getGoodsname());
				}		
		}
		this.delete(ids);
		logService.recordInfo("删除数据库字典", "成功",user.getLoginName(), request.getRemoteAddr());
		return WebResult.success();
	}
	
	
	/**
	 * 根据id获取
	 * */
	public OfficeTable getPro(Integer id) {
		OfficeTable office =this.getById(id);
		return office;
	}
	
	
	
	/**
	 * 确认编辑
	 * */
	public JSONObject executeEdit(HttpServletRequest request, OfficeTable office,User user) {
		log.error("into officeService executeEdit");
		OfficeTable oldoffice =this.getById(office.getId());
		OfficeTable officeKey =this.getByProperties("goodsname", office.getGoodsname());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldoffice)){
			//判断是否修改
			if(ObjectUtil.equalProperty(office, oldoffice, new String[]{"id","goodsname","warehouseamount","units","warehousename","supplier","types","pictures","thewarehousepeople","thestoragetime","lastborrower","lastborrowtime","remarks"})){
				return WebResult.NoChange();
			}//判断标识是否存在
			else if(officeKey!=null&&officeKey.getId()!=office.getId()&&officeKey.getGoodsname().equals(office.getGoodsname())){
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("标识已经存在，请重新输入");
			}else{//修改
				oldoffice.setGoodsname(office.getGoodsname());
				oldoffice.setWarehousename(office.getWarehousename());
				oldoffice.setWarehouseamount(office.getWarehouseamount());
				oldoffice.setSupplier(office.getSupplier());
				oldoffice.setUnits(office.getUnits());
				oldoffice.setTypes(office.getTypes());
				oldoffice.setThewarehousepeople(office.getThewarehousepeople());
				oldoffice.setThestoragetime(office.getThestoragetime());
				oldoffice.setLastborrower(office.getLastborrower());
				oldoffice.setLastborrowtime(office.getLastborrowtime());
				oldoffice.setPictures(office.getPictures());
				oldoffice.setRemarks(office.getRemarks());
				update(oldoffice);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(office.getGoodsname()))){
					ApplicationCommon.SYSCOMMONS.remove(office.getGoodsname());
					ApplicationCommon.SYSCOMMONS.put(office.getGoodsname(),office.getGoodsname());
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
	 * 执行入库
	 */
	public JSONObject executeAddIntoWare(HttpServletRequest request, OfficeTable office,User user){
		log.error("into officeService executeAddIntoWare");
		OfficeTable oldoffice =this.getById(office.getId());
		OfficeTable officeKey =this.getByProperties("goodsname", office.getGoodsname());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldoffice)){
			//判断标识是否存在
		 if(officeKey!=null&&officeKey.getId()!=office.getId()&&officeKey.getGoodsname().equals(office.getGoodsname())){
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("标识已经存在，请重新输入");
			}else{//修改
				
				oldoffice.setWarehouseamount(office.getWarehouseamount()+oldoffice.getWarehouseamount());								
				oldoffice.setRemarks(office.getRemarks());
				oldoffice.setThestoragetime(getCurDateString());
				oldoffice.setThewarehousepeople(office.getThewarehousepeople());
				update(oldoffice);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(office.getGoodsname()))){
					ApplicationCommon.SYSCOMMONS.remove(office.getGoodsname());
					ApplicationCommon.SYSCOMMONS.put(office.getGoodsname(),office.getGoodsname());
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
	 * 执行出库
	 */
	public JSONObject executeOutofWare(HttpServletRequest request, OfficeOut office,User user){
		log.error("into executeOutofWare");
		OfficeTable oldoffice =this.getById(office.getId());
		OfficeTable officeKey =this.getByProperties("goodsname", office.getGoodsname());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldoffice)){
			//判断标识是否存在
		 if(officeKey!=null&&officeKey.getId()!=office.getId()&&officeKey.getGoodsname().equals(office.getGoodsname())){
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("标识已经存在，请重新输入");
			}else{//修改
				log.error(office.getBorwarehouseamount());
				log.error(oldoffice.getWarehouseamount());
				oldoffice.setWarehouseamount(oldoffice.getWarehouseamount()-office.getBorwarehouseamount());								
				oldoffice.setLastborrower(office.getLastborrower());
				oldoffice.setLastborrowtime(getCurDateString());
				update(oldoffice);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(office.getGoodsname()))){
					ApplicationCommon.SYSCOMMONS.remove(office.getGoodsname());
					ApplicationCommon.SYSCOMMONS.put(office.getGoodsname(),office.getGoodsname());
				}
				logService.recordInfo("编辑数据字典","成功", user.getLoginName(), request.getRemoteAddr());
				return WebResult.success();
			}
		}else{
			logService.recordInfo("编辑数据字典","失败（尝试编辑不存在的数据字典）", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error(ControllerCommon.UNAUTHORIZED_ACCESS);
		}
	}
	
	public String getCurDateString (){
		Date currentTimeDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeString = sdf.format(currentTimeDate);
		return timeString;
	}
}
