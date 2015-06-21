package com.escala.xfer.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MethodInfo {
	private String name = "";
	private List<String> params = new ArrayList<String>();
	
	public MethodInfo() {
		this("");
	}
	
	public MethodInfo(String methodName) {
		this.name = methodName;
	}

	public void addParam(String paramType, String paramName) {
		params.add(paramType + " " + paramName);		
	}
	
	public void addParam(String paramType) {
		params.add(paramType);		
	}
	
	public List<String> getParams() {
		return params;		
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
