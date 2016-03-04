package org.zframework.core.web.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

public class CustomWebBindingInitializer implements WebBindingInitializer{

	public void initBinder(WebDataBinder binder, WebRequest request) {
		DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
		CustomDateEditor dateEditor = new CustomDateEditor(fmt, true);  
		binder.registerCustomEditor(Date.class, dateEditor);
	}
	
}
