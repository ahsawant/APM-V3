package com.escala.agent.boot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
//import java.nio.file.Files;
//import java.nio.file.StandardCopyOption;

public class AgentShell {

	private static final String AGENT_BINARY = "agent.jar";
	private static final String CORE_AGENT = "com.escala.agent.boot.CoreAgent";
	private static final String AGENTMAIN = "agentmain";
	private static final String PREMAIN = "premain";
	private static final String AGENT_JAR_SUFIX = "Agent";
	private static final String AGENT_JAR_PREFIX = "Jar";

	private static URLClassLoader loader = null;

	// Used when started after Java process is running
	public static void agentmain(String agentArgs, Instrumentation inst) {
		route(agentArgs, inst, AGENTMAIN);
	}

	// Used w/ command line
	public static void premain(String agentArgs, Instrumentation inst) {
		route(agentArgs, inst, PREMAIN);
	}

	private static void route(String agentArgs, Instrumentation inst,
			String methodName) {

		URL agentURL = ClassLoader.getSystemResource(AGENT_BINARY);
		// The loader is NOT closed on purpose (it's unclear when all
		// classes will have been loaded, so we "leak" this one
		System.out.println("URL: " + agentURL.toString());
		try {

			// Just need something to syncrhonize on (can't be loader as it's
			// null)
			synchronized (CORE_AGENT) {

				if (loader == null) {
					// Could have used Files.createTempFile() for more secure
					// applications
					File agentJar = File.createTempFile(AGENT_JAR_PREFIX,
							AGENT_JAR_SUFIX);

					InputStream inStream = agentURL.openStream();
					Files.copy(inStream, agentJar.toPath(),
							StandardCopyOption.REPLACE_EXISTING);
					inStream.close();

					// TODO: Not quite sure when a loader is closed (ignoring
					// for now)
					loader = new URLClassLoader(new URL[] { agentJar.toURI()
							.toURL() }, AgentShell.class.getClassLoader());
				}
			}

			Class<?> claz = loader.loadClass(CORE_AGENT);
			Method method = claz.getMethod(methodName, new Class[] {
					String.class, Instrumentation.class });
			System.out.println("Passing control to actual agent");
			method.invoke(null, agentArgs, inst);

		} catch (ClassNotFoundException e) {
			System.err.println("Fatal error loading Escala agent!");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.err.println("Fatal error loading Escala agent!");
			e.printStackTrace();
		} catch (SecurityException e) {
			System.err.println("Fatal error loading Escala agent!");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.err.println("Fatal error loading Escala agent!");
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("Fatal error loading Escala agent!");
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			System.err.println("Fatal error loading Escala agent!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Fatal error loading Escala agent!");
			e.printStackTrace();
		}

	}
}
