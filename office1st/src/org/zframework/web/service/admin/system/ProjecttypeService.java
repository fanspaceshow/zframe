package org.zframework.web.service.admin.system;

import java.util.List;

import org.springframework.stereotype.Service;
import org.zframework.web.entity.system.Projecttype;
import org.zframework.web.service.BaseService;


@Service
public class ProjecttypeService extends BaseService<Projecttype>{
    
	/**
	 * 获取项目类型的list
	 */
	public  List<Projecttype> getprojecttype(){
		//获取数据库中所有的数据项
		List<Projecttype> list = list();
		return list;
	}
}
