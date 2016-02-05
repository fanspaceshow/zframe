package org.zframework.core.web.filter;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.HttpServletUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.entity.system.User;

/**
 * 后台过滤器
 * 过滤未登录的用户
 * @author ZENGCHAO
 *
 */
public class AdminInterceptor implements HandlerInterceptor{
	/**
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
		String requestUrl = request.getRequestURI().replace(request.getRequestURI().substring(0, request.getRequestURI().indexOf(request.getContextPath())+request.getContextPath().length()), "");
		if(requestUrl.startsWith("/admin")){
			if(!requestUrl.startsWith("/admin/login")){
				if(ObjectUtil.isNotNull(request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN))){
					//判断是否存在于ApplicationCommon中
					User cUser = (User) request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
					if(!ApplicationCommon.LOGIN_USERS.contains(cUser.getLoginName())){
						//在服务器热重启的情况下 ApplicationCommon中的数据丢失
						ApplicationCommon.LOGIN_USERS.add(cUser.getLoginName());
						ApplicationCommon.LOGIN_SESSIONS.put(cUser.getLoginName(), request.getSession());
					}
				}else{
					//判断是否是ajax请求，如果不是ajax请求则直接重定向
					if(ObjectUtil.isNull(request.getHeader("x-requested-with")))
					{
						response.sendRedirect(request.getContextPath()+"/admin/login");
					}
					else if(HttpServletUtil.isAjaxWithRequest(request) && request.getMethod().equals("GET"))
					{
						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.println("<script type=\"text/javascript\">");
						out.println("top.Dialog.alert(\"登陆超时!\",function(){");
						out.println("window.location.href='"+request.getContextPath()+"/admin/login/loginOut'");
						out.println("});");
						out.println("</script>");
						out.close();
					}else if(HttpServletUtil.isAjaxWithRequest(request) && request.getMethod().equals("POST")){
						//超时
						response.sendError(408);
					}
					return false;
				}
			}else if(!requestUrl.startsWith("/admin/login/loginOut")){//如果是登陆页面，判断当前的session是否已经登陆，如果已经登陆则直接转向首页
				if(ObjectUtil.isNotNull(request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN))){
					response.sendRedirect(request.getContextPath()+"/admin/index");
				}
			}
		}
		return true;
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
