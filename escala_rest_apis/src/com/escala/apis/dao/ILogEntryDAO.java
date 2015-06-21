package com.escala.apis.dao;

import java.util.List;
import com.escala.xfer.ILogEntries;
import com.escala.xfer.ILogEntry;
import com.escala.xfer.LogEntry;


public interface ILogEntryDAO {
	public int getCount();
	
	public List<LogEntry> getEntries();
	public List<LogEntry> getEntries(int start, int end);
	public List<LogEntry> getModified (boolean clear);
	public List<LogEntry> getModified (int start, int end, boolean clear);
	
	public boolean addLogEntry(ILogEntry entry);
	public boolean addLogEntries(ILogEntries entry);
	public boolean addLogEntry(String logData);
	
	public boolean removeLogEntry(int id);
	public boolean removeLogRange(int start, int end);
	
	public ILogEntry getLogEntry(int index);
}
