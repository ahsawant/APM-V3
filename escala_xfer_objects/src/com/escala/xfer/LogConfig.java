package com.escala.xfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogConfig implements ILogConfig {
	private String classInfo;
	private String methodInfo;
	private String handlerClass;
	private int status;

	public LogConfig() {
		this(null, null, null, ILogConfig.NEW);
	}

	public LogConfig(String classInfo, String methodInfo) {
		this(classInfo, methodInfo, null, ILogConfig.NEW);
	}

	public LogConfig(String className, String methodName, String handlerClass) {
		this(className, methodName, handlerClass, ILogConfig.NEW);
	}

	public LogConfig(String classInfo, String methodInfo, String handlerClass,
			int status) {
		super();
		this.classInfo = classInfo;
		this.methodInfo = methodInfo;
		this.handlerClass = handlerClass;
		this.status = status;
	}

	public LogConfig(ILogConfig logEntry) {
		this(logEntry.getClassInfo(), logEntry.getMethodInfo(), logEntry
				.getHandlerClass(), logEntry.getStatus());
	}

	public String getHandlerClass() {
		return handlerClass;
	}

	public void setHandlerClass(String handlerClass) {
		this.handlerClass = handlerClass;
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
