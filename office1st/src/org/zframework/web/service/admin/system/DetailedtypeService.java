package org.zframework.web.service.admin.system;


import java.util.List;

import org.springframework.stereotype.Service;
import org.zframework.web.entity.system.Detailedtype;
import org.zframework.web.service.BaseService;

@Service
public class DetailedtypeService extends BaseService<Detailedtype>{
	
	/**
	 * 获取项目类型的详细信息
	 */
	public List<Detailedtype> getlist(String projecttype){
		String Hql="from Detailedtype where projecttype=?";		
		List<Detailedtype> list=listForEntity(Hql,projecttype);
		return list;	
	}
}
