package com.escala.xfer.parser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JavaFile {
	private String name;
	private List<ClassInfo> classes = new ArrayList<ClassInfo>();
	
	public JavaFile() {
		this("");
	}
	
	public JavaFile(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void addClass(ClassInfo classInfo) {
		classes.add(classInfo);
	}
	
	public List<ClassInfo> getClasses() {
		return classes;
	}
	
	
}
