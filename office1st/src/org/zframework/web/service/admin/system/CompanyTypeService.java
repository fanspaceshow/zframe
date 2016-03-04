package org.zframework.web.service.admin.system;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.zframework.web.entity.system.CompanyType;

import org.zframework.web.service.BaseService;

@Service
public class CompanyTypeService extends BaseService<CompanyType> {
	@Autowired
	private LogService logService;
	Log log  = LogFactory.getLog("CompanyTypeService");
	
	/**
	 * 获取数据的list
	 */
	public  List<CompanyType> getCompanyType(){
		//获取数据库中所有的数据项
		List<CompanyType> list = list();
		return list;
	}
}
