package org.zframework.core.web.support;

/**
 * 
 * @author zengchao
 *
 */
public class ControllerCommon {
	/**
	 * 错误
	 */
	public final static String ERROR = "error";
	/**
	 * 非法访问
	 */
	public final static String UNAUTHORIZED_ACCESS = "UnauthorizedAccess";
	/**
	 * 无权访问
	 */
	public final static String NO_PERMISSION = "NoPermission";
	/**
	 * 资源停用
	 */
	public final static String RES_DISABLED = "ResDisabled";
	/**
	 * 自定义错误类，使用前先调用setError;如果需要执行script脚本，请setScript
	 * 直接return getViewName即可
	 * @author zengchao
	 *
	 */
	public static class CustomError{
		private static String error = null;
		private static String script = null;
		/**
		 * 
		 * @return
		 */
		public static String getViewName(){
			return "CustomError_CustomError";
		}
		public static void setError(String err){
			error = err;
		}
		/**
		 * 获取错误信息
		 * 获取一次后清空
		 * @return
		 */
		public static String getError(){
			String err = error;
			error = null;
			return err;
		}
		
		public static String getScript() {
			String s = script;
			script = null;
			return s;
		}
		/**
		 * 
		 * @param script
		 */
		public static void setScript(String script) {
			CustomError.script = script;
		}
		/**
		 * 判断是否有脚本
		 * @return
		 */
		public static boolean NullScript(){
			return script == null;
		}
		public static boolean NullError() {
			return error==null;
		}
	}
}
