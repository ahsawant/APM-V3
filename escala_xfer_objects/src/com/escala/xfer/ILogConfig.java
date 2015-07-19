package com.escala.xfer;

public interface ILogConfig {
	public static final int NEW = 0;
	public static final int MODIFIED = 1;
	public static final int DELETED = 2;
	public static final int REPORTED = 3;

	String getClassInfo();

	void setClassInfo(String classInfo);

	String getMethodInfo();

	void setMethodInfo(String methodInfo);

	String getHandlerClass();

	void setHandlerClass(String handlerClass);

	int getStatus();

	void setStatus(int status);
}
