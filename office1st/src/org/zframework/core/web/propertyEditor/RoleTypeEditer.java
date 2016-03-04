package org.zframework.core.web.propertyEditor;

import java.beans.PropertyEditorSupport;

import org.zframework.web.entity.system.type.RoleType;

public class RoleTypeEditer extends PropertyEditorSupport{
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		Object value = null;
		RoleType[] roleTypes = RoleType.values();
		for(RoleType roleType : roleTypes){
			if(roleType.toString().equals(text.toUpperCase())){
				value = roleType;
				break;
			}
		}
		super.setValue(value);
	}
}
