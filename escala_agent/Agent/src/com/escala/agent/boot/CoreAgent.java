package com.escala.agent.boot;

import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.escala.agent.asm.SufixClassTransformer;
import com.escala.agent.handlers.CalledMethodHandler;
import com.escala.agent.log.CalledMethod;
import com.escala.agent.log.LogClassTransformer;
import com.escala.agent.log.LogUpdate;
import com.escala.system.staticm.CalledMethodProxy;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class CoreAgent {

	// Define a static logger
	static final Logger logger = LogManager
			.getLogger(CoreAgent.class.getName());

	// Used when started after Java process is running
	public static void agentmain(String agentArgs, Instrumentation inst) {
		premain(agentArgs, inst);
	}

	// Used w/ command line
	public static void premain(String agentArgs, Instrumentation inst) {

		CalledMethodProxy.setListener(new CalledMethodHandler());

		List<String> args = parseArguments(agentArgs);

		(new Thread(new AgentRuntime(inst))).start();

		inst.addTransformer(new SufixClassTransformer(args.get(0)), true);
	}

	private static final List<String> parseArguments(String agentArgs) {

		if (logger.isTraceEnabled())
			logger.trace("Agent arguments: " + agentArgs);
		StringTokenizer st = new StringTokenizer(agentArgs, ";");
		ArrayList<String> args = new ArrayList<String>();
		while (st.hasMoreElements())
			args.add(st.nextToken());
		return (args);
	}

	private static class AgentRuntime implements Runnable {

		private Instrumentation inst;
		private LogUpdate logRefresher;
		private LogClassTransformer transformer;
		private WebResource service;

		private AgentRuntime(Instrumentation inst) {
			this.inst = inst;

			service = initService();
			logRefresher = new LogUpdate(inst, service);
			transformer = new LogClassTransformer(
					logRefresher.getClassEntries());
			inst.addTransformer(transformer, true);
			CalledMethod.init(service);
		}

		public void run() {
			logger.error("In CoreAgent.AgentRuntime.run()");

			short serial = 0;
			while (true) {
				logger.info("Agent loop running");
				Short serialClass = new Short(serial);
				logRefresher.doRefresh(serialClass);
				logRefresher.doRestransform(transformer.getClassLoaders(),
						serialClass);
				// Do this once a second
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error("Thread interrupted.", e);
				}
				serial++;
				if (serial == Short.MAX_VALUE)
					serial = 0;
			}
		}

		private WebResource initService() {

			// The classloader hackery below is because of Jersey
			// http://jersey.576304.n2.nabble.com/Client-Help-td5156690.html
			ClassLoader old = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(
						this.getClass().getClassLoader());

				return (Client.create(new DefaultClientConfig())
						.resource(getBaseURI()));

			} finally {
				Thread.currentThread().setContextClassLoader(old);
			}

		}

		private static URI getBaseURI() {
			return UriBuilder.fromUri("http://localhost:8080/escala/apis")
					.build();
		}

	}

}
