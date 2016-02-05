package org.zframework.web.service.admin.system;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.zframework.web.entity.system.DataDict;
import org.zframework.web.service.BaseService;

@Service
public class DataDictService extends BaseService<DataDict> {
	/**
	 * 根据ID获取
	 * 立即加载DataDictType属性
	 * @param id
	 * @return
	 */
	public DataDict getById_NoLazy(Integer id){
		DataDict dd = getById(id);
		Hibernate.initialize(dd.getDataDictType());
		return dd;
	}
}
