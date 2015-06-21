package com.escala.api_tester;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.escala.xfer.LogConfigs;
import com.escala.xfer.LogConfig;
import com.escala.xfer.LogEntries;
import com.escala.xfer.LogEntry;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class Test {

	private final static int CLASS_COUNT = 10;
	private final static int METHOD_COUNT = 10;
	private final static int LOG_ENTRY_COUNT = 10;

	public static void main(String[] args) {
		
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		WebResource service = client.resource(getBaseURI());

//		testGetModifiedConfigs(service);
		testAddLogEntries(service);
//		testDeleteLogEntries(service);

		testAddLogConfigs(service);
		
//		testGetLogConfigs(service);
		
//		testGetModifiedConfigs(service);
		
//		testDeleteLogConfigs(service);

	}

	private static void testDeleteLogEntries(WebResource service) {
		ClientResponse response = service.path("logentries").delete(
				ClientResponse.class);
		System.out.println(response.getStatus());

	}

	private static void testAddLogEntries(WebResource service) {
		LogEntry logEntry = null;

		String baseLogStatement = "Hello World ";

		LogEntries entries = new LogEntries();
		for (int i = 0; i < Test.LOG_ENTRY_COUNT; i++) {
			String logData = baseLogStatement + i;
			logEntry = new LogEntry(logData);
			entries.getLogEntries().add(logEntry);
		}
		ClientResponse response = service.path("logentries")
				.accept(MediaType.APPLICATION_JSON)
				.put(ClientResponse.class, entries);
		// Return code should be 201 == created resource
		System.out.println(response.getStatus());

		// Get the logentries
		entries = service.path("logentries")
				.accept(MediaType.APPLICATION_JSON).get(LogEntries.class);
		String countRes = service.path("logentries/count")
				.accept(MediaType.TEXT_PLAIN).get(String.class);

		if (entries != null && entries.getLogEntries() != null
				&& countRes != null) {
			int count = Integer.parseInt(countRes);
			if (count == entries.getLogEntries().size()
					&& count == Test.LOG_ENTRY_COUNT) {
				System.out.println("Succeeded on creating " + count
						+ " log entries");
			}
		}

	}

	private static void testGetModifiedConfigs(WebResource service) {
		// Test get all instances
		LogConfigs entries = service.path("logconfigs/modified")
				.queryParam("clear", "true").accept(MediaType.APPLICATION_JSON)
				.get(LogConfigs.class);
		if (entries != null && entries.getLogConfigs() != null) {
			for (LogConfig entry : entries.getLogConfigs()) {
				System.out.println("Found a modified log entry for:\n"
						+ "Class: " + entry.getClassInfo() + "\nMethod:"
						+ entry.getMethodInfo() + "\nStatus: "
						+ entry.getStatus());
			}
		}

	}

	private static void testDeleteLogConfigs(WebResource service) {
		LogConfigs entries = service.path("logconfigs")
				.accept(MediaType.APPLICATION_JSON).get(LogConfigs.class);
		if (entries != null && entries.getLogConfigs() != null) {
			for (LogConfig entry : entries.getLogConfigs()) {
				ClientResponse response = service.path(
						"logconfigs/" + entry.getClassInfo() + "/"
								+ entry.getMethodInfo()).delete(
						ClientResponse.class);
				System.out.println(response.getStatus());
			}
		}

	}

	private static void testGetLogConfigs(WebResource service) {
		// Test get all instances
		LogConfigs entries = service.path("logconfigs")
				.accept(MediaType.APPLICATION_JSON).get(LogConfigs.class);
		if (entries != null && entries.getLogConfigs() != null) {
			for (LogConfig entry : entries.getLogConfigs()) {
				System.out.println("Found a log entry for:\n" + "Class: "
						+ entry.getClassInfo() + "\nMethod:"
						+ entry.getMethodInfo());
			}
		}

		// Test get log entries for class using URI
		String baseClassName = "com.escala.apis.dao.DAOFactory";
		for (int i = 0; i < Test.CLASS_COUNT; i++) {
			String classInfo = baseClassName + i;
			entries = service.path("logconfigs/" + classInfo)
					.accept(MediaType.APPLICATION_JSON).get(LogConfigs.class);

			System.out.println("Found the following log entries for class "
					+ classInfo + ":");
			for (LogConfig entry : entries.getLogConfigs()) {
				System.out.println("Method: " + entry.getMethodInfo());
			}
		}

		// Test get log entries for class & method using URI
		baseClassName = "com.escala.apis.dao.DAOFactory";
		String baseMethodName = "getInstance";
		for (int i = 0; i < Test.CLASS_COUNT; i++) {
			String classInfo = baseClassName + i;
			for (int j = 0; j < Test.METHOD_COUNT; j++) {
				String methodInfo = baseMethodName + j;
				entries = service
						.path("logconfigs/" + classInfo + "/" + methodInfo)
						.accept(MediaType.APPLICATION_JSON)
						.get(LogConfigs.class);
				if (entries != null && entries.getLogConfigs() != null
						&& entries.getLogConfigs().size() > 0) {
					System.out.println("Found log entries for class "
							+ classInfo + " and method " + methodInfo);
				}
			}
		}

		// Test get log entries for class & method using query attributes
		baseClassName = "com.escala.apis.dao.DAOFactory";
		baseMethodName = "getInstance";
		for (int i = 0; i < Test.CLASS_COUNT; i++) {
			String classInfo = baseClassName + i;
			for (int j = 0; j < Test.METHOD_COUNT; j++) {
				String methodInfo = baseMethodName + j;
				LogConfig entry = service.path("logconfigs/instance")
						.queryParam("class", classInfo)
						.queryParam("method", methodInfo)
						.accept(MediaType.APPLICATION_JSON).get(LogConfig.class);

				if (entry != null) {
					System.out.println("Found log entry for class "
							+ entry.getClassInfo() + " and method "
							+ entry.getMethodInfo());
				}
			}
		}

	}

	private static void testAddLogConfigs(WebResource service) {
		
		LogConfig logEntry = null;

		String baseClassName = "com.escala.apis.dao.DAOFactory";
		String baseMethodName = "getInstance";

		// Add a bunch of log configs
		for (int i = 0; i < Test.CLASS_COUNT; i++) {
			String classInfo = baseClassName + i;
			for (int j = 0; j < Test.METHOD_COUNT; j++) {
				String methodInfo = baseMethodName + j;
				String path = "logconfigs/" + classInfo + "/" + methodInfo;
				logEntry = new LogConfig(classInfo, methodInfo);
				ClientResponse response = service.path(path)
						.accept(MediaType.APPLICATION_JSON)
						.put(ClientResponse.class, "");
				// Return code should be 201 == created resource
				System.out.println(response.getStatus());
			}
		}

		// Get the logconfigs
		LogConfigs entries = service.path("logconfigs")
				.accept(MediaType.APPLICATION_JSON).get(LogConfigs.class);
		String countRes = service.path("logconfigs/count")
				.accept(MediaType.TEXT_PLAIN).get(String.class);

		if (entries != null && entries.getLogConfigs() != null
				&& countRes != null) {
			int count = Integer.parseInt(countRes);
			if (count == entries.getLogConfigs().size()
					&& count == CLASS_COUNT * METHOD_COUNT) {
				System.out.println("Succeeded on creating " + count
						+ " log entries");
			}
		}

	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/escala/apis").build();
	}
}
