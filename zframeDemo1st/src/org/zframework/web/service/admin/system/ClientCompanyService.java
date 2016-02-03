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

import org.zframework.web.entity.system.Client;
import org.zframework.web.entity.system.Project;
import org.zframework.web.entity.system.User;
import org.zframework.web.service.BaseService;
@Service
public class ClientCompanyService extends BaseService<Client>  {
	@Autowired
	private LogService logService;
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
}
