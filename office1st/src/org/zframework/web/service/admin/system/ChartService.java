package org.zframework.web.service.admin.system;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.zframework.web.service.BaseService;

@Service
public class ChartService extends BaseService<Object>{
	
	public Map<String,String> getOperaChart(){
		Map<String,String> map = new LinkedHashMap<String, String>();
		Query query = baseDao.getQuery("SELECT manipulateName,COUNT(id) FROM Log GROUP BY manipulateName");
		List<?> list = query.list();
		for(Object obj : list){
			Object[] array = (Object[]) obj;
			map.put(array[0].toString(), array[1].toString());
		}
		return map;
	}
}
