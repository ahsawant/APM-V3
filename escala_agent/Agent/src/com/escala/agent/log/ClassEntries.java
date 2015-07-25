package com.escala.agent.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.escala.agent.log.ClassEntryWrapper.ModifiedClass;

public class ClassEntries {

	// Define a static logger
	static final Logger logger = LogManager.getLogger(ClassEntries.class
			.getName());

	private final Map<String, ClassEntryWrapper> classEntries = new HashMap<String, ClassEntryWrapper>();

	private boolean hasChanged = true;

	int oldCount = classEntries.size();

	public boolean addIfNeeded(String claz, String method, String handlerClass,
			Short serial) {
		ClassEntryWrapper classEntry = classEntries.get(claz);
		if (classEntry == null)
			classEntry = new ClassEntryWrapper(claz);

		// This will always return true for a brand new classEntry
		if (classEntry.addIfNeeded(method, handlerClass, serial)) {
			classEntries.put(claz, classEntry);
			hasChanged = true;
		}
		logger.debug("Method name: " + claz + "::" + method + "; Has Changed: "
				+ hasChanged);
		return (hasChanged);
	}

	/**
	 * 
	 * @param reset
	 *            - resets the "changed" flag if passed in as true
	 * @return
	 */
	public boolean hasChanged(boolean reset) {
		boolean oldChanged = hasChanged;
		if (reset)
			hasChanged = false;
		return (oldChanged);
	}

	public Iterator<ModifiedClass> changedClassesIterator(Short serial) {
		if (oldCount != classEntries.size()) {
			oldCount = classEntries.size();
			logger.error("ClassEntries count changed to: "
					+ classEntries.size());
		}
		return (new ClassEntryWrapperIterator(classEntries.values().iterator(),
				serial));
	}

	public boolean contains(String className) {
		return (classEntries.containsKey(className));
	}

	public ModifiedClass get(String claz) {
		ClassEntryWrapper entry = classEntries.get(claz);
		if (entry != null)
			return (entry.getModifiedClass());
		return (null);
	}

	private static final class ClassEntryWrapperIterator implements
			Iterator<ModifiedClass> {

		private Iterator<ClassEntryWrapper> iter;
		private ClassEntryWrapper next = null;
		private Short serial;

		private ClassEntryWrapperIterator(Iterator<ClassEntryWrapper> iter,
				Short serial) {
			this.iter = iter;
			this.serial = serial;
		}

		@Override
		public boolean hasNext() {
			boolean retVal = false;
			logger.debug("In hasNext!");
			if (next != null)
				retVal = true;
			while (iter.hasNext()) {
				next = iter.next();
				if (logger.isDebugEnabled())
					logger.debug("next was: " + retVal);
				if (next.hasChanged(true, serial)) {
					retVal = true;
					break;
				}
			}
			if (logger.isDebugEnabled())
				logger.debug("RetVal was: " + retVal);
			return retVal;
		}

		@Override
		public ModifiedClass next() {
			if (next != null || hasNext()) {
				ClassEntryWrapper entry = next;
				next = null;
				return (entry.getModifiedClass());
			}
			throw (new NoSuchElementException());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
