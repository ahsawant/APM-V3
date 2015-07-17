package com.escala.agent.log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.escala.system.staticm.ICalledMethod;
import com.escala.xfer.LogEntries;
import com.escala.xfer.LogEntry;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CalledMethod implements ICalledMethod {

	// Define a static logger
	static final Logger logger = LogManager.getLogger(CalledMethod.class
			.getName());

	private static ICalledMethod cMethod;
	private WebResource service;

	public static void statCalledMethod(Object targetObject, String methodName,
			Object[] args) {
		System.err.println("Instrumented code called.********************************************************");
		if (cMethod != null)
			cMethod.calledMethod(targetObject, methodName, args);
	}

	public static void init(WebResource service) {
		cMethod = new CalledMethod(service);
	}

	private CalledMethod(WebResource service) {
		this.service = service;
	}

	@Override
	public void calledMethod(Object targetObject, String methodName,
			Object[] args) {
		LogEntry logEntry = null;
		System.err.println("Instrumented code called.********************************************************");


		// String baseLogStatement = CalledMethodProxy.getDebugString(
		// targetObject, methodName, args);

		LogEntries entries = new LogEntries();
		logEntry = new LogEntry(getOutput(targetObject, methodName, args));
		entries.getLogEntries().add(logEntry);
		
		System.err.println("Method called *****************************************************************");

		ClientResponse response = service.path("logentries")
				.accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, entries);
		// Return code should be 201 == created resource
		if (response.getStatus() != 201)
			logger.error(response.getStatus());
	}

	private String getOutput(Object targetObject, String methodName,
			Object[] args) {

		StringBuilder string = new StringBuilder();

		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss,SSS");
		Date date = new Date();
		string.append(dateFormat.format(date));

		// Print the target object class if dynamic call
		if (targetObject != null) {
			string.append("  DYNAMIC  ");
			string.append(targetObject.getClass().getName());
		} else {
			string.append("  STATIC  ");
		}
		string.append("::");
		string.append(methodName);
		string.append("():\n");

		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				if (!args[i].getClass().isArray())
					string.append("       Arg" + i + ":   "
							+ args[i].getClass().getName() + "\n        "
							+ args[i] + "\n");
				else {
					string.append("Array arguments currently not supported!");

				}
			} else
				string.append("Arg" + i + ": <NULL>\n");
		}
		return (string.append("\n").toString());
	}
}
