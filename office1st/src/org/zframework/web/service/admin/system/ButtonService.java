package org.zframework.web.service.admin.system;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.WebResult;
import org.zframework.web.entity.system.Button;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;

@Service
public class ButtonService extends BaseService<Button>{
	@Autowired
	private LogService logService;
	
	public JSONObject executeAdd(HttpServletRequest request, Button button,User user) {
		JSONObject jResult = new JSONObject();
		Button eqBtn = getByName(button.getName());
		if(ObjectUtil.isNull(eqBtn)){
			eqBtn = getByProperties("field", button.getField());
			if(ObjectUtil.isNull(eqBtn)){
				save(button);
				jResult = WebResult.success();
				logService.recordInfo("新增按钮","成功",user.getLoginName() , request.getRemoteAddr());
			}else{
				jResult = WebResult.error("此字段名称已存在!");
				//记录日志
				logService.recordInfo("新增按钮","失败(此按钮字段名称已经存在!)",user.getLoginName() , request.getRemoteAddr());
			}
		}else{
			jResult = WebResult.error("此按钮名称已存在!");
			//记录日志
			logService.recordInfo("新增按钮","失败(此按钮名称已经存在!)",user.getLoginName() , request.getRemoteAddr());
		}
		return jResult;
	}
	public JSONObject executeEdit(HttpServletRequest request, Button button,
			BindingResult result,User user) {
		JSONObject jResult = new JSONObject();
		Button eqBtn = getById(button.getId());
		if(ObjectUtil.isNotNull(eqBtn)){
			//判断是否有改变
			if(ObjectUtil.equalProperty(button, eqBtn, new String[]{"name","field","icon","enabled"})){
				jResult = WebResult.NoChange();
			}else if(ObjectUtil.isNotNull(this.get(Restrictions.eq("name", button.getName()),Restrictions.not(Restrictions.eq("id", eqBtn.getId()))))){
				jResult = WebResult.error("按钮名称已存在!");
				logService.recordInfo("编辑按钮","失败(按钮名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			}else if(ObjectUtil.isNotNull(this.get(Restrictions.eq("field", button.getField()),Restrictions.not(Restrictions.eq("id", eqBtn.getId()))))){
				jResult = WebResult.error("字段名称已存在!");
				logService.recordInfo("编辑按钮","失败(字段名称已经存在)", user.getLoginName(), request.getRemoteAddr());
			}else{
				eqBtn.setName(button.getName());
				eqBtn.setField(button.getField());
				eqBtn.setIcon(button.getIcon());
				eqBtn.setEnabled(button.getEnabled());
				
				update(eqBtn);
				jResult = WebResult.success();
				logService.recordInfo("编辑按钮","成功", user.getLoginName(), request.getRemoteAddr());
			}
		}else{
			jResult = WebResult.error("按钮已不存在!");
			logService.recordInfo("编辑按钮","失败(尝试编辑不存的按钮)", user.getLoginName(), request.getRemoteAddr());
		}
		return jResult;
	}
	/**
	 * 执行删除操作
	 * @param ids
	 * @return
	 */
	public JSONObject executeDelete(Integer[] ids) {
		JSONObject jResult = new JSONObject();
		//记录日志
		List<Button> btnList = getByIds(ids);
		for(Button btn : btnList){
			if(ObjectUtil.isNull(btn)){
				jResult = WebResult.error("尝试删除不存在的按钮");
				logService.recordInfo("删除按钮","失败（尝试删除不存在的角色）",getCurrentUser().getLoginName() , getRequest().getRemoteAddr());
				break;
			}else{
				delete(btn);
				jResult = WebResult.success();
				logService.recordInfo("删除按钮","成功（按钮ID:"+btn.getId()+",按钮名称:"+btn.getName()+")",getCurrentUser().getLoginName() , getRequest().getRemoteAddr());
			}
		}
		return jResult;
	}
	/**
	 * 锁定
	 * @param ids
	 * @param type
	 * @return
	 */
	public JSONObject executeLockOrUnLockUser(Integer[] ids,int type){
		JSONObject jResult = new JSONObject();
		String hql = null;
		String resultTag = "isLocked";
		if(type == 0){
			hql = "update Button set enabled=1 where id in ("+StringUtil.toSameString("?", ids.length, ",")+")";
		}else if(type == 1){
			resultTag = "isUnLocked";
			hql = "update Button set enabled=0 where id in ("+StringUtil.toSameString("?", ids.length, ",")+")";
		}
		if(ObjectUtil.isNotNull(hql)){
			int result = baseDao.execteBulk(hql, ids);
			if(result>0){
				jResult.element(resultTag, true);
			}else{
				jResult.element(resultTag, false);
				jResult.element("error", "操作失败，请稍后重试!");
			}
		}else{
			jResult.element(resultTag, false);
			jResult.element("error", "非法操作!");
		}
		return jResult;
	}
}
