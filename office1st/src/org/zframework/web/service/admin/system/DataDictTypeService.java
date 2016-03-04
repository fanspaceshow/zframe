package org.zframework.web.service.admin.system;

import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.springframework.stereotype.Service;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.entity.system.DataDictType;
import org.zframework.web.service.BaseService;

@Service
public class DataDictTypeService extends BaseService<DataDictType>{
	/**
	 * 获取字典数据名称类，立即加载延迟属性
	 * @param criterions
	 * @return
	 */
	public DataDictType get_NoLazy(Criterion...criterions){
		DataDictType ddt = get(criterions);
		if(ObjectUtil.isNotNull(ddt)){
			Hibernate.initialize(ddt.getDatadicts());
		}
		return ddt;
			
	}
}
