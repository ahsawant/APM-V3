package com.escala.agent.log;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.ConnectException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.escala.agent.log.ClassEntryWrapper.ModifiedClass;
import com.escala.xfer.LogConfig;
import com.escala.xfer.LogConfigs;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;

public class LogUpdate {

	// Define a static logger
	static final Logger logger = LogManager
			.getLogger(LogUpdate.class.getName());

	private final ClassEntries classEntries = new ClassEntries();
	private Instrumentation inst;
	private WebResource service;

	public LogUpdate(Instrumentation inst, WebResource service) {
		this.inst = inst;
		this.service = service;
	}

	public void doRefresh(Short serial) {
		try {

			LogConfigs entries = service.path("logconfigs")
					.accept(MediaType.APPLICATION_XML).get(LogConfigs.class);
			if (entries != null && entries.getLogConfigs() != null) {
				for (LogConfig entry : entries.getLogConfigs()) {
					if (classEntries.addIfNeeded(entry.getClassInfo(),
							entry.getMethodInfo(), serial))
						logger.error("Found a new log entry for: "
								+ entry.getClassInfo() + "::"
								+ entry.getMethodInfo() + "; Status: "
								+ entry.getStatus() + "; classHandler:"
								+ entry.getHandlerClass());
				}
			}
		} catch (ClientHandlerException e) {
			if (e.getCause().getClass() == ConnectException.class)
				logger.error("Failed to connect to server.", e);
			else
				logger.error("Unexpected error!", e);

		} catch (Exception e) {
			logger.error("Unexpected error!", e);
		}
	}

	public void doRestransform(Map<String, Set<ClassLoader>> loaders,
			Short serial) {

		// logger.error("Retransforming...");

		// Need to retransform changed classes
		for (Iterator<ModifiedClass> iter = classEntries
				.changedClassesIterator(serial); iter.hasNext();) {
			ModifiedClass claz = iter.next();
			try {
				logger.error("Retransforming class: " + claz.getClassName());
				synchronized (loaders) {
					Set<ClassLoader> clazLoaders = loaders.get(claz
							.getClassName().replace(".", "/"));
					if (clazLoaders != null)
						for (Iterator<ClassLoader> iterator = clazLoaders
								.iterator(); iterator.hasNext();)
							inst.retransformClasses(Class.forName(
									claz.getClassName(), true, iterator.next()));
					else
						// Use System loader
						inst.retransformClasses(Class.forName(
								claz.getClassName(), true,
								ClassLoader.getSystemClassLoader()));
				}
				logger.error("Restransformed class " + claz.getClassName());
			} catch (ClassNotFoundException e) {
				logger.error("Class not retransformed!", e);
			} catch (UnmodifiableClassException e) {
				logger.error("Class not retransformed!", e);
			}
		}
	}

	public ClassEntries getClassEntries() {
		return classEntries;
	}
}
