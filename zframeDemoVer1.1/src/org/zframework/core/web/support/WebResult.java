package org.zframework.core.web.support;

import net.sf.json.JSONObject;

/**
 * 异步响应类型
 * @author zengchao
 *
 */
public  class WebResult {
	/**
	 * 没有任何改变
	 * @return
	 */
	public static JSONObject NoChange(){
		JSONObject obj = new JSONObject();
		obj.element("NoChanges", true);
		return obj;
	}
	/**
	 * 需要验证密码
	 * @return
	 */
	public static JSONObject NeedVerifyPassword(){
		JSONObject obj = new JSONObject();
		obj.element("NeedVerifyPassword", true);
		return obj;
	}
	/**
	 * 操作成功
	 * @return
	 */
	public static JSONObject success(){
		JSONObject obj = new JSONObject();
		obj.element("success", true);
		return obj;
	}
	/**
	 * 操作失败
	 * @param 错误提示 msg
	 * @return
	 */
	public static JSONObject error(String msg){
		JSONObject obj = new JSONObject();
		obj.element("success", false);
		obj.element("error", msg);
		return obj;
	}
}

