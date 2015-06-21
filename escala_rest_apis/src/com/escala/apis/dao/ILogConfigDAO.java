package com.escala.apis.dao;

import java.util.List;

import com.escala.xfer.ILogConfig;
import com.escala.xfer.ILogConfigs;
import com.escala.xfer.LogConfig;

public interface ILogConfigDAO {
	public int getCount();
	public int getClassCount();
	public int getMethodCount(String className);
	
	public List<LogConfig> getAll();
	public List<LogConfig> getModified (boolean clear);
	public List<LogConfig> getAllForClass(String classInfo);
	
	public boolean addLogConfig(ILogConfig entry);
	public boolean addLogConfigs(ILogConfigs entry);
	public boolean addLogConfig(String className, String methodName);
	
	public boolean removeLogConfig(ILogConfig entry);
	public boolean removeLogConfig(String className, String methodName);
	
	public boolean hasLogConfig(ILogConfig entry);
	public ILogConfig getLogConfig(String classInfo, String methodInfo);
}

