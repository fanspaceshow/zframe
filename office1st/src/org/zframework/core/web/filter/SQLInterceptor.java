package org.zframework.core.web.filter;

import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.zframework.core.util.HttpServletUtil;
import org.zframework.core.util.ObjectUtil;

/**
 * 防止SQL注入过滤器
 * 过滤request参数中含有数据库关键字的访问请求，转向非法参数页面
 * @author ZENGCHAO
 *
 */
public class SQLInterceptor implements HandlerInterceptor{
	private final static Log log = LogFactory.getLog(SQLInterceptor.class);
	//private final String SQL_KEYWORDS = "'|and|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|,";
	private final String SQL_KEYWORDS = "'|*|%|;";
	/**
	 * 检测参数中是否含有SQL关键字
	 * 如果含有则转向错误页面!
	 * 
	 * 在业务处理器处理之前调用
	 * 如果返回false
	 *    则从当前的处理器往回执行afterCompletion(),再退出拦截连
	 * 如果返回true
	 *    执行下一个拦截器，知道所有拦截器你执行完毕
	 *    在执行业务处理器Controller
	 *    然后进入拦截器链
	 *    从最后一个拦截器往回执行所有的postHandle()
	 *    接着再从最后一个拦截器往回执行所有的afterCompletion()
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object object) throws Exception {
		Enumeration<?> paramNames = request.getParameterNames();
		while(paramNames.hasMoreElements()){
			String paramName = paramNames.nextElement().toString();
			String[] paramValues = request.getParameterValues(paramName);
			for(String paramValue : paramValues){
				if(hasSQLKeyword(paramValue)){
					log.error("发现非法参数!", new Throwable("SQL注入攻击。参数名:"+paramName+"，参数值:"+paramValue));
					//判断是否是ajax请求，如果不是ajax请求则直接重定向
					if(ObjectUtil.isNull(request.getHeader("x-requested-with")))
					{
						response.sendRedirect(request.getContextPath()+"/admin/login/loginOut");
					}
					else if(HttpServletUtil.isAjaxWithRequest(request))
					{
						if(request.getHeader("referer").endsWith("/admin/login")){
							response.sendError(400, "数据传输过程中检测到非法参数!");
						}else{
							response.setContentType("text/html");
							PrintWriter out = response.getWriter();
							out.println("<script type=\"text/javascript\">");
							out.println("top.Dialog.alert(\"数据传输过程中检测到非法参数!\",function(){");
							out.println("window.location.href='"+request.getContextPath()+"/admin/login/loginOut'");
							out.println("});");
							out.println("</script>");
							out.close();
						}
					}
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * 判断是否含有SQL关键字
	 * @param paramValue
	 * @return
	 */
	private boolean hasSQLKeyword(String paramValue){
		String[] keywords = SQL_KEYWORDS.split("\\|");
		for(String key : keywords){
			if(paramValue.indexOf(key)>=0){
				return true;
			}
		}
		return false;
	}
	/**
	 * 在业务处理器处理完成之后执行
	 * 在生成视图操作之前执行
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object object, ModelAndView mav) throws Exception {
	}
	/**
	 * 在DispatcherServlet完全处理完请求后调用
	 *    如果发生异常，则会从当前的拦截器往回执行所有的afterCompletion	
	 */
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object object, Exception exception)
			throws Exception {
		
	}
}
