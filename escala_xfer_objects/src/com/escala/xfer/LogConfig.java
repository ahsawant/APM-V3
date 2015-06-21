package com.escala.xfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogConfig implements ILogConfig {
	private String classInfo;
	private String methodInfo;
	private int status;
	
	public LogConfig() {
		this(null, null, ILogConfig.NEW);		
	}
	
	public LogConfig(String classInfo, String methodInfo) {
		this(classInfo, methodInfo, ILogConfig.NEW);
	}
	
	public LogConfig(String classInfo, String methodInfo, int status) {
		super();
		this.classInfo = classInfo;
		this.methodInfo = methodInfo;
		this.status = status;
	}
	
	public LogConfig(ILogConfig logEntry) {
		this(logEntry.getClassInfo(), logEntry.getMethodInfo(), logEntry.getStatus());
	}
	
	public String getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(String classInfo) {
		this.classInfo = classInfo;
	}

	public String getMethodInfo() {
		return methodInfo;
	}

	public void setMethodInfo(String methodInfo) {
		this.methodInfo = methodInfo;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;		
	}

	@Override
	public int getStatus() {
		return status;
	}

}
