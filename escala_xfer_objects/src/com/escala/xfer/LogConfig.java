package com.escala.xfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogConfig implements ILogConfig {
	private String classInfo;
	private String methodInfo;
	private String handlerClassName;
	private String handlerClassMethodToCall;
	private String handlerClass;
	private int status;

	public LogConfig() {
		this(null, null, null, null, null, ILogConfig.NEW);
	}

	public LogConfig(String classInfo, String methodInfo) {
		this(classInfo, methodInfo, null, null, null, ILogConfig.NEW);
	}

	public LogConfig(String className, String methodName,
			String handlerClassName, String handlerClassMethodToCall,
			String handlerClass) {
		this(className, methodName, handlerClassName, handlerClassMethodToCall,
				handlerClass, ILogConfig.NEW);
	}

	public LogConfig(String classInfo, String methodInfo,
			String handlerClassName, String handlerClassMethodToCall,
			String handlerClass, int status) {
		super();
		this.classInfo = classInfo;
		this.methodInfo = methodInfo;
		this.handlerClassName = handlerClassName;
		this.handlerClassMethodToCall = handlerClassMethodToCall;
		this.handlerClass = handlerClass;
		this.status = status;
	}

	public LogConfig(ILogConfig logEntry) {
		this(logEntry.getClassInfo(), logEntry.getMethodInfo(), logEntry
				.getHanderClassName(), logEntry.getHandlerClassMethodToCall(),
				logEntry.getHandlerClass(), logEntry.getStatus());
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

	@Override
	public String getHanderClassName() {
		return handlerClassName;
	}

	@Override
	public void setHanderClassName(String handlerClassName) {
		this.handlerClassName = handlerClassName;
	}

	@Override
	public String getHandlerClassMethodToCall() {
		return handlerClassMethodToCall;
	}

	@Override
	public void setHandlerClassMethodToCall(String handlerClassMethodToCall) {
		this.handlerClassMethodToCall = handlerClassMethodToCall;
	}

}
