package com.escala.xfer.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClassInfo {

	private String name = "";
	private List<MethodInfo> methods = new ArrayList<MethodInfo>();
		
	public ClassInfo() {
		this("");
	}
	
	public ClassInfo(String className) {
		this.name = className;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addMethod(MethodInfo methodInfo) {
		methods.add(methodInfo);
		
	}
	
	public List<MethodInfo> getMethods() {
		return methods;		
	}

}
