package com.escala.xfer;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LogConfigs implements ILogConfigs {
	List<LogConfig> entries = null;
	
	public LogConfigs() {
		entries = new ArrayList<LogConfig>();
	}
	
	public LogConfigs(List<LogConfig> entries) {
		this.entries = entries;
	}

	@Override
	public List<LogConfig> getLogConfigs() {
		return entries;
	}

	@Override
	public void setLogConfigs(List<LogConfig> entries) {
		this.entries = entries;		
	}
	
	
}
