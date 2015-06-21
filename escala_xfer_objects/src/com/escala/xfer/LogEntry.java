package com.escala.xfer;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogEntry implements ILogEntry {

	private String logData;
	private int status;
	
	public LogEntry() {
		this(null, ILogEntry.NEW);
	}
	
	public LogEntry(String logData) {
		this(logData, ILogEntry.NEW);
	}
	
	public LogEntry(String logData, int status) {
		super();
		this.logData = logData;
		this.status = status;
	}
	
	public LogEntry(ILogEntry logEntry) {
		this(logEntry.getLogData(),logEntry.getStatus());
	}
	
	@Override
	public String getLogData() {
		return logData;
	}

	@Override
	public void setLogData(String logInfo) {
		this.logData = logInfo;
	}

	@Override
	public int getStatus() {
		return status;
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
	}

}
