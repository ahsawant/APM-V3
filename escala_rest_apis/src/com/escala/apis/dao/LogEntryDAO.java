package com.escala.apis.dao;

import java.util.ArrayList;
import java.util.List;

import com.escala.xfer.ILogEntries;
import com.escala.xfer.ILogEntry;
import com.escala.xfer.LogEntry;

public class LogEntryDAO implements ILogEntryDAO {

	private List<ILogEntry> logEntries = null;
	private List<ILogEntry> modified = null;

	public LogEntryDAO() {
		logEntries = new ArrayList<ILogEntry>();
		modified = new ArrayList<ILogEntry>();
	}

	@Override
	public int getCount() {
		return logEntries.size();
	}

	@Override
	public List<LogEntry> getEntries(int start, int end) {
		List<LogEntry> result = new ArrayList<LogEntry>();

		// If end is undefined, return everything up to the end of the list
		if (end == -1) {
			end = logEntries.size() - 1;
		}

		if (end >= start && end < logEntries.size()) {
			for (int i = start; i <= end; i++) {
				result.add((LogEntry) logEntries.get(i));
			}
		}
		return result;
	}

	@Override
	public List<LogEntry> getModified(int start, int end, boolean clear) {
		List<LogEntry> result = new ArrayList<LogEntry>();

		// If end is undefined, return everything up to the end of the list
		if (end == -1) {
			end = modified.size() - 1;
		}

		if (end >= start && end < modified.size()) {
			for (int i = start; i <= end; i++) {
				if (clear) {
					ILogEntry entry = modified.get(start);
					result.add(new LogEntry(entry));
					entry.setStatus(ILogEntry.REPORTED);
					modified.remove(start);
				} else {
					result.add((LogEntry) modified.get(i));
				}
			}
		}
		return result;
	}

	@Override
	public List<LogEntry> getEntries() {
		return getEntries(0, logEntries.size() - 1);
	}

	@Override
	public List<LogEntry> getModified(boolean clear) {
		return getModified(0, modified.size() - 1, clear);
	}

	@Override
	public boolean addLogEntry(ILogEntry entry) {
		boolean result = true;
		entry.setStatus(ILogEntry.NEW);
		modified.add(entry);
		logEntries.add(entry);
		return result;
	}

	@Override
	public boolean addLogEntry(String logData) {
		return addLogEntry(new LogEntry(logData));
	}

	@Override
	public boolean removeLogEntry(int index) {
		return removeLogRange(index, index);
	}

	@Override
	public boolean removeLogRange(int start, int end) {
		boolean result = false;
		// If end is undefined, return everything up to the end of the list
		if (end == -1) {
			end = logEntries.size() - 1;
		}
		
		if (start <= end && end < logEntries.size()) {
			for (int i = start; i <= end; i++) {
				ILogEntry entry = logEntries.remove(start);
				if (entry != null) {
					result = true;
					entry.setStatus(ILogEntry.DELETED);
					modified.add(entry);
				}
			}
		}
		return result;
	}

	@Override
	public ILogEntry getLogEntry(int id) {
		ILogEntry result = null;
		if (id < logEntries.size()) {
			result = logEntries.get(id);
		}
		return result;
	}

	@Override
	public boolean addLogEntries(ILogEntries entries) {
		boolean result = false;
		if (entries != null && entries.getLogEntries().size() > 0) {
			result = true;
			for (ILogEntry logEntry : entries.getLogEntries()) {
				result &= addLogEntry(logEntry);
			}
		}

		return result;
	}

}
