package com.escala.apis.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.escala.xfer.ILogConfig;
import com.escala.xfer.ILogConfigs;
import com.escala.xfer.LogConfig;

public class LogConfigDAO implements ILogConfigDAO {
	// Dictionary of log entries indexed by the class name
	// MAP(Class Name) -> MAP (Method Name) -> ILogConfig
	private Map<String, Map<String, ILogConfig>> logConfigs = null;
	private Map<String, ILogConfig> modified = null;

	public LogConfigDAO() {
		logConfigs = new HashMap<String, Map<String, ILogConfig>>();
		modified = new HashMap<String, ILogConfig>();
	}

	@Override
	public int getCount() {
		int count = 0;
		// Iterate over the list of all classes being instrumented
		for (Map.Entry<String, Map<String, ILogConfig>> entry : logConfigs
				.entrySet()) {
			if (entry != null) {
				// If the entry for the class is valid, get the size of the
				// methods map associated to the class entry and compute to
				// the running count
				Map<String, ILogConfig> methodMap = entry.getValue();
				if (methodMap != null) {
					count += methodMap.keySet().size();
				}
			}
		}
		return count;
	}

	@Override
	public int getClassCount() {
		// Figure out how many class entries are in the class set
		return logConfigs.keySet().size();
	}

	@Override
	public int getMethodCount(String className) {
		// First see if the class is present in the map
		int count = 0;
		Map<String, ILogConfig> methodMap = logConfigs.get(className);
		if (methodMap != null) {
			count += methodMap.keySet().size();
		}
		return count;
	}

	@Override
	public boolean addLogConfig(ILogConfig entry) {
		boolean result = false;

		if (entry != null && !hasLogConfig(entry)) {
			System.out
					.println("No previous entry found, so starting to add entry "
							+ entry);
			Map<String, ILogConfig> methods = logConfigs.get(entry
					.getClassInfo());
			// Check if an entry for the specified class already exists on the
			// map
			if (methods != null) {
				System.out.println("Found previous entries for class "
						+ entry.getClassInfo() + ".");
				// Check if an entry for the method already exists
				ILogConfig method = methods.get(entry.getMethodInfo());
				if (method == null) {
					System.out.println("Storing log entry for method "
							+ entry.getMethodInfo() + " in class hash map.");
					// If an entry for the method does not yet exist, add the
					// new entry to the map
					methods.put(entry.getMethodInfo(), entry);
					result = true;
				} else {
					System.out.println("An entry for " + entry
							+ " already exists");
				}
			} else {
				System.out.println("No previous entries for class "
						+ entry.getClassInfo()
						+ ". Adding collection for class methods.");
				// if entry for the class does not exist, add one
				methods = new HashMap<String, ILogConfig>();
				// And finally associate the new log entry with the map indexed
				// by the method info
				System.out.println("Storing log config for method "
						+ entry.getMethodInfo() + " in class hash map.");
				methods.put(entry.getMethodInfo(), entry);
				// Finally store the class hash map on the class dictionary
				logConfigs.put(entry.getClassInfo(), methods);
				result = true;
			}
		}

		// If entry was successfully added, add entry to the modified list
		if (result) {
			addToModified(entry, ILogConfig.NEW);
		}

		return result;
	}

	private void addToModified(ILogConfig entry, int status) {
		entry.setStatus(status);
		modified.put(entry.getClassInfo() + "##" + entry.getMethodInfo(), entry);
	}

	@Override
	public boolean addLogConfig(String className, String methodName) {
		return (addLogConfig(className, methodName, null, null, null));
	}

	@Override
	public boolean addLogConfig(String className, String methodName,
			String handlerClassName, String handlerClassMethodToCall,
			String handlerClass) {
		boolean result = false;
		if (className != null && className != "" && methodName != null
				&& methodName != "") {
			result = addLogConfig(new LogConfig(className, methodName,
					handlerClassName, handlerClassMethodToCall, handlerClass));
		}
		return result;
	}

	@Override
	public boolean removeLogConfig(ILogConfig entry) {
		boolean result = false;

		if (entry != null && hasLogConfig(entry)) {
			Map<String, ILogConfig> methods = logConfigs.get(entry
					.getClassInfo());
			// Check if an entry for the specified class already exists on the
			// map
			if (methods != null) {
				entry = methods.remove(entry.getMethodInfo());
				result = true;
			}
		}

		if (result) {
			addToModified(entry, ILogConfig.DELETED);
		}
		return result;
	}

	@Override
	public boolean removeLogConfig(String className, String methodName) {
		boolean result = false;
		if (className != null && className != "" && methodName != null
				&& methodName != "") {
			result = removeLogConfig(new LogConfig(className, methodName));
		}
		return result;
	}

	@Override
	public boolean hasLogConfig(ILogConfig entry) {
		boolean result = false;
		System.out.println("Checking to see if log config " + entry
				+ " already exists");
		if (entry != null) {
			Map<String, ILogConfig> methods = logConfigs.get(entry
					.getClassInfo());
			// Check if an entry for the specified class already exists on the
			// map
			if (methods != null) {
				// Check if an entry for the method already exists
				ILogConfig method = methods.get(entry.getMethodInfo());
				if (method != null) {
					result = true;
				}
			}
		}
		return result;
	}

	@Override
	public List<LogConfig> getAll() {
		List<LogConfig> result = new ArrayList<LogConfig>();
		// Iterate over the list of all classes being instrumented
		for (Map.Entry<String, Map<String, ILogConfig>> entry : logConfigs
				.entrySet()) {
			if (entry != null) {
				// If the entry for the class is valid, get the size of the
				// methods map associated to the class entry and compute to
				// the running count
				Map<String, ILogConfig> methodMap = entry.getValue();
				for (ILogConfig logConfig : methodMap.values()) {
					result.add((LogConfig) logConfig);
				}
			}
		}
		return result;
	}

	@Override
	public List<LogConfig> getAllForClass(String classInfo) {
		List<LogConfig> result = new ArrayList<LogConfig>();

		if (classInfo != null && classInfo != "") {
			Map<String, ILogConfig> classEntry = logConfigs.get(classInfo);
			if (classEntry != null) {
				for (ILogConfig logConfig : classEntry.values()) {
					result.add((LogConfig) logConfig);
				}
			}
		}
		return result;
	}

	@Override
	public ILogConfig getLogConfig(String classInfo, String methodInfo) {
		ILogConfig result = null;
		if (classInfo != null && classInfo != "") {
			Map<String, ILogConfig> classEntry = logConfigs.get(classInfo);
			if (classEntry != null) {
				result = classEntry.get(methodInfo);
			}
		}
		return result;
	}

	@Override
	public List<LogConfig> getModified(boolean clear) {
		List<LogConfig> result = new ArrayList<LogConfig>();

		for (ILogConfig entry : modified.values()) {
			result.add(new LogConfig(entry));
			if (clear) {
				entry.setStatus(ILogConfig.REPORTED);
			}
		}

		if (clear) {
			modified.clear();
		}

		return result;
	}

	@Override
	public boolean addLogConfigs(ILogConfigs entries) {
		boolean result = false;
		if (entries != null && entries.getLogConfigs().size() > 0) {
			result = true;
			for (ILogConfig logConfig : entries.getLogConfigs()) {
				result &= addLogConfig(logConfig);
			}
		}

		return result;
	}
}
