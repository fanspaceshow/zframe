package org.zframework.core.web.filter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.HttpServletUtil;
import org.zframework.core.util.ObjectUtil;
import org.zframework.core.util.StringUtil;
import org.zframework.core.web.support.ControllerCommon;
import org.zframework.web.entity.system.Button;
import org.zframework.web.entity.system.Resource;
import org.zframework.web.entity.system.Role;
import org.zframework.web.entity.system.User;

/**
 * 添加每次请求中一些公用的参数
 * @author ZENGCHAO
 *
 */
public class ParamInitInterceptor implements HandlerInterceptor{
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
		//项目基本路径
		String basePath = request.getContextPath().replace("/", "");
		ApplicationCommon.BASEPATH = basePath;
		if(basePath.trim().length()>0){
			basePath = "/"+ basePath;
		}
		request.setAttribute("basePath", basePath);
		
		//初始化后台管理系统资源按钮权限
		String requestUrl = request.getRequestURI().replace(request.getRequestURI().substring(0, request.getRequestURI().indexOf(request.getContextPath())+request.getContextPath().length()), "");
        //取消对url的/admin开头的检查 
		/**		if(requestUrl.startsWith("/admin")){*/
			if(!requestUrl.startsWith("/admin/login") && !requestUrl.startsWith("/admin/index") && !requestUrl.startsWith("/admin/error")){
				Resource curAccessRes = null;//当前访问的资源
				User currentUser = (User) request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
				//判断当前用户是否是系统管理员，如果是，则无需添加按钮权限，拥有所有按钮权限
				List<Role> roles = currentUser.getRoles();
				boolean isSystemadmin = false;
				for(Role role : roles){
					if(role.getName().equals(ApplicationCommon.SYSCOMMONS.get("SystemRole"))){
						isSystemadmin = true;
						break;
					}
				}
				if(!isSystemadmin && !HttpServletUtil.isAjaxWithRequest(request)){
					for(Resource res : currentUser.getResources()){
						if((requestUrl).startsWith(res.getUrl())){
							curAccessRes = res;
							break;
						}
					}
					if(ObjectUtil.isNotNull(curAccessRes)){//如果存在访问的资源
						//此资源拥有的按钮
						List<Button> resourceButtons = curAccessRes.getButtons();
						//获取当前用户在当前资源中所拥有的按钮权限
						List<Button> buttonPermissions = new ArrayList<Button>();
						for(Resource res : currentUser.getResourcesBtns()){
							if(res.getId() == curAccessRes.getId()){
								buttonPermissions = res.getButtons();
								break;
							}
						}
						StringBuffer setButtonsRightStr = new StringBuffer();
						//资源拥有的按钮权限
						StringBuffer strResourceButtons = new StringBuffer();
						//用户拥有的按钮权限
						StringBuffer strUserResourceButtons = new StringBuffer();
						
						for(Button btn : resourceButtons){
							strResourceButtons.append(","+btn.getField());
						}
						for(Button btn : buttonPermissions){
							strUserResourceButtons.append(","+btn.getField());
						}
						if(strResourceButtons.length()>0)
							setButtonsRightStr.append("<input type=\"hidden\" id=\"ResourceButtons\" value=\""+strResourceButtons.substring(1)+"\"/><br/>");
						else
							setButtonsRightStr.append("<input type=\"hidden\" id=\"ResourceButtons\" value=\"\"/><br/>");
						
						if(strUserResourceButtons.length()>0)
							setButtonsRightStr.append("<input type=\"hidden\" id=\"UserResourceButtons\" value=\""+strUserResourceButtons.substring(1)+"\"/>");
						else
							setButtonsRightStr.append("<input type=\"hidden\" id=\"UserResourceButtons\" value=\"\"/>");
						request.setAttribute("ButtonPrrmission", setButtonsRightStr.toString());
					}
				}
			}
			//存储用户的皮肤信息
			if(!requestUrl.startsWith("/admin/login")){
				User currentUser = (User) request.getSession().getAttribute(ApplicationCommon.SESSION_ADMIN);
				request.setAttribute("theme", StringUtil.isEmpty(currentUser.getPageStyle().trim())?"default":currentUser.getPageStyle().trim());
				request.setAttribute("UserName", currentUser.getRealName());
			}
	//	}
		return true;
	}
	/**
	 * 在业务处理器处理完成之后执行
	 * 在生成视图操作之前执行
	 */
	public void postHandle(HttpServletRequest request, HttpServletResponse response,
			Object object, ModelAndView mav) throws Exception {
		if(ObjectUtil.isNotNull(mav)){
			if(ObjectUtil.isNotNull(mav.getViewName())){
				if(mav.getViewName().equals(ControllerCommon.UNAUTHORIZED_ACCESS)){
					if(HttpServletUtil.isAjaxWithRequest(request)){
						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.println("<script type=\"text/javascript\">");
						out.println("top.Dialog.alert(\"非访访问!\",function(){");
						out.println("window.location.href='"+request.getContextPath()+"/admin/login/loginOut'");
						out.println("});");
						out.println("</script>");
						out.close();
					}else{
						response.sendRedirect("/"+request.getContextPath()+"/admin/error/e/"+ControllerCommon.UNAUTHORIZED_ACCESS);
					}
					mav.clear();
				}else if(mav.getViewName().equals(ControllerCommon.NO_PERMISSION)){//无权访问
					if(HttpServletUtil.isAjaxWithRequest(request)){
						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.println("<script type=\"text/javascript\">");
						out.println("top.Dialog.error(\"无权访问!\");");
						out.println("</script>");
						out.close();
					}else{
						response.sendRedirect("/"+request.getContextPath()+"/admin/error/e/"+ControllerCommon.NO_PERMISSION);
					}
					mav.clear();
				}else if(mav.getViewName().equals(ControllerCommon.ERROR)){
					if(HttpServletUtil.isAjaxWithRequest(request)){
						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.println("<script type=\"text/javascript\">");
						out.println("top.Dialog.alert('错误',\"系统出错!\",'error');");
						out.println("</script>");
						out.close();
					}else{
						response.sendRedirect("/"+request.getContextPath()+"/admin/error/e/"+ControllerCommon.ERROR);
					}
					mav.clear();
				}else if(mav.getViewName().equals(ControllerCommon.CustomError.getViewName())){
					if(HttpServletUtil.isAjaxWithRequest(request)){
						response.setContentType("text/html");
						PrintWriter out = response.getWriter();
						out.println("<script type=\"text/javascript\">");
						out.println("top.Dialog.alert('错误',\""+ControllerCommon.CustomError.getError()+"\",'error');");
						if(!ControllerCommon.CustomError.NullScript()){
							out.println(ControllerCommon.CustomError.getScript());
						}
						out.println("</script>");
						out.close();
					}else{
						response.sendRedirect("/"+request.getContextPath()+"/admin/error/e/"+ControllerCommon.ERROR);
					}
					mav.clear();
				}
			}
		}
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
