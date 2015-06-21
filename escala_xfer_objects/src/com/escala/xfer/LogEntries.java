package com.escala.xfer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class LogEntries implements ILogEntries {
	List<LogEntry> logEntries = null;
	
	public LogEntries() {
		this(new ArrayList<LogEntry>());
	}
	
	public LogEntries(List<LogEntry> entries) {
		super();
		this.logEntries = entries;
	}
	
	@Override
	public List<LogEntry> getLogEntries() {
		return logEntries;
	}

	@Override
	public void setLogEntries(List<LogEntry> entries) {
		this.logEntries = entries;
	}

}
