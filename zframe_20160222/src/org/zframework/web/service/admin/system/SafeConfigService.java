package org.zframework.web.service.admin.system;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.zframework.core.support.ApplicationCommon;
import org.zframework.core.util.ObjectUtil;
import org.zframework.web.entity.system.SafeConfig;
import org.zframework.web.entity.system.type.IPRoleType;
import org.zframework.web.service.BaseService;

@Service
public class SafeConfigService extends BaseService<SafeConfig>{
	public void initSafeConfig(){
		//ip策略
		String safeIpConfig = getSaveConfigValue(getApplicationCommon("SafeIpConfig"));
		if(ObjectUtil.isNull(safeIpConfig)){
			ApplicationCommon.IPROLETYPE = IPRoleType.Deny;
		}else{
			if(safeIpConfig.toLowerCase().equals("allow")){
				ApplicationCommon.IPROLETYPE = IPRoleType.Allow;
			}else{
				ApplicationCommon.IPROLETYPE = IPRoleType.Deny;
			}
		}
		//ip安全开关
		String ipRoleState = getSaveConfigValue(getApplicationCommon("IpRoleState"));
		if(ObjectUtil.isNull(ipRoleState)){
			ApplicationCommon.IPROLE = false;
		}else{
			if(ipRoleState.toLowerCase().equals("true")){
				ApplicationCommon.IPROLE = true;
			}else{
				ApplicationCommon.IPROLE = false;
			}
		}
	}
	/**
	 * ip策略为允许
	 * @param type
	 * @return
	 */
	public JSONObject executeChangeSafeIpType(int type) {
		JSONObject jResult = new JSONObject();
		String name = getApplicationCommon("SafeIpConfig");
		SafeConfig safeConfig = getByName(name);
		String value = "Allow";
		if(type == 1)
			value = "Deny";
		if(ObjectUtil.isNotNull(safeConfig)){
			safeConfig.setValue(value);
			update(safeConfig);
			jResult.element("isChanged", true);
		}else{
			safeConfig = new SafeConfig();
			safeConfig.setName(name);
			safeConfig.setValue(value);
			save(safeConfig);
			jResult.element("isChanged", true);
		}
		if(type == 1)
			ApplicationCommon.IPROLETYPE = IPRoleType.Deny;
		else
			ApplicationCommon.IPROLETYPE = IPRoleType.Allow;
		return jResult;
	}
	/**
	 * 获取安全配置项值
	 * @param name
	 * @return
	 */
	public String getSaveConfigValue(String name){
		String value = null;
		SafeConfig safeConfig = getByName(name);
		if(ObjectUtil.isNotNull(safeConfig)){
			value = safeConfig.getValue();
		}
		return value;
	}
	/**
	 * 开启或者关闭IP安全规则
	 * @param state
	 * @return
	 */
	public JSONObject executeOpenOrCloseIpRole(boolean state) {
		JSONObject jResult = new JSONObject();
		String name = getApplicationCommon("IpRoleState");
		SafeConfig safeConfig = getByName(name);
		if(ObjectUtil.isNotNull(safeConfig)){
			safeConfig.setValue(state+"");
			update(safeConfig);
			jResult.element("isChanged", true);
		}else{
			safeConfig = new SafeConfig();
			safeConfig.setName(name);
			safeConfig.setValue(state+"");
			save(safeConfig);
			jResult.element("isChanged", true);
		}
		if(safeConfig.getValue().toLowerCase().equals("true"))
			ApplicationCommon.IPROLE = true;
		else
			ApplicationCommon.IPROLE = false;
		return jResult;
	}
}
