package org.zframework.core.web.resolver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

public class BeanArgumentResolver implements WebArgumentResolver {

	public Object resolveArgument(MethodParameter param, NativeWebRequest request) throws Exception {
		RequestBean requestBean = param.getParameterAnnotation(RequestBean.class);
		if (requestBean != null) {
			String _param = requestBean.value();
			if (_param.equals("_def_param_name")) {
				_param = param.getParameterName();
			}
			Class<?> clazz = param.getParameterType();
			Object object = clazz.newInstance();
			HashMap<String, String[]> paramsMap = new HashMap<String, String[]>();
			Iterator<String> itor = request.getParameterNames();
			while (itor.hasNext()) {
				String webParam = (String) itor.next();
				String[] webValue = request.getParameterValues(webParam);
				if (webParam.startsWith(_param)) {
					paramsMap.put(webParam, webValue);
				}
			}
			BeanWrapper obj = new BeanWrapperImpl(object);
			obj.registerCustomEditor(Date.class, null, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
			for (String propName : paramsMap.keySet()) {
				String[] propVals = paramsMap.get(propName);
				String[] props = propName.split("\\.");
				if (props.length == 2) {
					obj.setPropertyValue(props[1], propVals);
				} else if (props.length == 3) {
					Object tmpObj = obj.getPropertyValue(props[1]);
					if (tmpObj == null)
						obj.setPropertyValue(props[1], obj.getPropertyType(props[1]).newInstance());
					obj.setPropertyValue(props[1] + "." + props[2], propVals);
				}

			}
			return object;
		} else {
			return WebArgumentResolver.UNRESOLVED;
		}
	}
}
