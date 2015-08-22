package com.escala.agent.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

final class ClassEntryWrapper {

	private final String claz;
	private boolean changed = true;
	private final Map<String, Short> methodEntries = new HashMap<String, Short>();
	private String handlerClassSource;
	private String handlerClassName;
	private String handerClassMethodToCall;

	// Define a static logger
	static final Logger logger = LogManager.getLogger(ClassEntryWrapper.class
			.getName());

	ClassEntryWrapper(String claz) {
		this.claz = claz;
	}

	/**
	 * 
	 * @param method
	 *            - Name of method
	 * @param handlerClass
	 * @param serial
	 *            - Current serial number
	 * @return - whether the method was added (true) or existed (false)
	 */
	public boolean addIfNeeded(String method, String handlerClassName,
			String handerClassMethodToCall, String handlerClass, Short serial) {
		Short oldSerial = methodEntries.put(method, serial);

		this.handlerClassName = handlerClassName;
		this.handerClassMethodToCall = handerClassMethodToCall;

		if (handlerClass != null)
			this.handlerClassSource = handlerClass;

		if (oldSerial == null) {
			changed = true;
			return (true);
		}
		return (false);
	}

	public Map<String, Short> getMethodEntries() {
		return methodEntries;
	}

	/**
	 * Checks if things have changed, but also resets the changed value!!!
	 * 
	 * @return
	 */
	public boolean hasChanged(boolean reset, Short serial) {
		deleteOldEntries(serial);
		boolean oldChanged = changed;
		if (reset)
			changed = false;
		return (oldChanged);
	}

	private void deleteOldEntries(Short serial) {
		Iterator<Map.Entry<String, Short>> iter = methodEntries.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Short> entry = iter.next();
			if (!serial.equals(entry.getValue())) {
				logger.error("Deleted entry: " + entry.getKey());
				iter.remove();
				changed = true;
			}
		}
	}

	public ModifiedClass getModifiedClass() {
		return (new ModifiedClass());
	}

	public class ModifiedClass {
		public String getClassName() {
			return (claz);
		}

		public boolean isMethodInstrumented(String methodName) {
			return (methodEntries.containsKey(methodName));
		}

		public String getHandlerClassName() {
			return (handlerClassName);
		}

		public String getHandlerClassMethodToCall() {
			return (handerClassMethodToCall);
		}

		public String getHandlerClassSource() {
			return (handlerClassSource);
		}

	}
}