package com.escala.xfer;

public interface ILogEntry {
	public static final int NEW = 0;
	public static final int MODIFIED = 1;
	public static final int DELETED = 2;
	public static final int REPORTED = 3;
	
	String getLogData();
	void setLogData(String logInfo);
	int getStatus();
	void setStatus(int status);
}
