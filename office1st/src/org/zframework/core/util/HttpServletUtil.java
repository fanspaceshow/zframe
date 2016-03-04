package org.zframework.core.util;

import javax.servlet.http.HttpServletRequest;

public class HttpServletUtil {
	/**
	 * 判断是否是ajax请求
	 * 根据请求头信息中的x-requested-with属性
	 * @param request
	 * @return
	 */
	public static boolean isAjaxWithRequest(HttpServletRequest request){
		if(ObjectUtil.isNotNull(request)){
			if(ObjectUtil.isNotNull(request.getHeader("x-requested-with"))){
				if(request.getHeader("x-requested-with").equals("XMLHttpRequest"))
					return true;
			}else if("true".equals(request.getParameter("_SYSTEMREQUEST_FROM_000"))){
				return true;
			}
			else{
				return false;
			}
		}
		return false;
	}
}
