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

import org.zframework.web.entity.system.GoodsList;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
@Service
public class GoodsnameService extends BaseService<GoodsList>  {
	@Autowired
	private LogService logService;
	Log log  = LogFactory.getLog("GoodsnameService");
	/**
	 * 初始化数据字典
	 * 将数据库中的数据项加载到系统内存中
	 */
	public void InitPros(){
		//获取数据库中所有的数据项
		List<GoodsList> list = list();
		ApplicationCommon.SYSCOMMONS.clear();
		if(ObjectUtil.isNotNull(list)){
			for(GoodsList goods : list){
				ApplicationCommon.SYSCOMMONS.put(goods.getGoodsname(),goods.getGoodsname());
			}
		}
}
	/**
	 * 分页显示SupplierList
	 * @param pageBean
	 * */
	public List<GoodsList> getproList(PageBean pageBean, String name, String value) {
		if(!StringUtil.isEmpty(name)){
			if("id".equals(name)){
				if(RegexUtil.isInteger(value)){
					 
					pageBean.addCriterion(Restrictions.idEq(Integer.parseInt(value)));
				}
			}else{
				
				pageBean.addCriterion(Restrictions.like(name, value+"%"));
			}
		}
		List<GoodsList> proList=this.listByPage(pageBean);
		return proList;
	}
	
	/***
	 * 执行增加
	 * @param request
	 * @param comm
	 * @param result
	 * @param user
	 * */
	public JSONObject executeAdd(HttpServletRequest request, GoodsList goods,User user) {
		GoodsList goodsByKey=this.getByProperties("goodsname",goods.getGoodsname());
		if(ObjectUtil.isNotNull(goodsByKey)){
			logService.recordInfo("新增数据字典","失败(标识名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			return WebResult.error("标识已经存在!");
		}else{
			save(goods);
			//更新缓存
			if(ObjectUtil.isNull(ApplicationCommon.SYSCOMMONS.get(goods.getGoodsname()))){
				ApplicationCommon.SYSCOMMONS.put(goods.getGoodsname(),goods.getGoodsname());
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
		List<GoodsList> list=this.getByIds(ids);
		for(int i=0;i<list.size();i++){
			GoodsList goods=list.get(i);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(goods.getGoodsname()))){
					ApplicationCommon.SYSCOMMONS.remove(goods.getGoodsname());
				}		
		}
		this.delete(ids);
		logService.recordInfo("删除数据库字典", "成功",user.getLoginName(), request.getRemoteAddr());
		return WebResult.success();
	}
	
	
	/**
	 * 根据id获取
	 * */
	public GoodsList getPro(Integer id) {
		GoodsList goods =this.getById(id);
		return goods;
	}
	
	
	/**
	 * 确认编辑
	 * */
	public JSONObject executeEdit(HttpServletRequest request, GoodsList goods,User user) {
		log.error("into GoodsList executeEdit");
		GoodsList oldgoods =this.getById(goods.getId());
		GoodsList goodsKey =this.getByProperties("goodsname", goods.getGoodsname());//根据传入的Key查看是否存在
		if(ObjectUtil.isNotNull(oldgoods)){
			//判断是否修改
			if(ObjectUtil.equalProperty(goods, oldgoods, new String[]{"id","goodsname","unit","type","remarks"})){
				return WebResult.NoChange();
			}//判断标识是否存在
			else if(goodsKey!=null&&goodsKey.getId()!=goods.getId()&&goodsKey.getGoodsname().equals(goods.getGoodsname())){
				logService.recordInfo("编辑数据字典","失败（重复标识）", user.getLoginName(), request.getRemoteAddr());
				return WebResult.error("标识已经存在，请重新输入");
			}else{//修改
				oldgoods.setGoodsname(goods.getGoodsname());
				oldgoods.setUnit(goods.getUnit());			
				oldgoods.setType(goods.getType());
				oldgoods.setRemarks(goods.getRemarks());				
				update(oldgoods);
				//更新缓存
				if(ObjectUtil.isNotNull(ApplicationCommon.SYSCOMMONS.get(goods.getGoodsname()))){
					ApplicationCommon.SYSCOMMONS.remove(goods.getGoodsname());
					ApplicationCommon.SYSCOMMONS.put(goods.getGoodsname(),goods.getGoodsname());
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
