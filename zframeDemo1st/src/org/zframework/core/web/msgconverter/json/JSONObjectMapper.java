package org.zframework.core.web.msgconverter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

public class JSONObjectMapper extends ObjectMapper{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4352074116272252209L;

	public JSONObjectMapper(){
		//this.registerModule(new JodaModule());
		this.registerModule(new Hibernate4Module());
		this.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
		this.enable(SerializationFeature.INDENT_OUTPUT);
	}
}
