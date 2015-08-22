package com.escala.agent.compiler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Compiler {

	// Define a static logger
	static final Logger logger = LogManager.getLogger(Compiler.class.getName());

	public Compiler() {

	}

	public static void compile(String fullyQualifiedClassName,
			String classHandlerMethodToCall, String classSource, ClassLoader cl)
			throws CompilationException {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager standardJavaFileManager = compiler
				.getStandardFileManager(null, null, null);
		final CustomClassloaderJavaFileManager fileManager = new CustomClassloaderJavaFileManager(
				cl, standardJavaFileManager);

		Set<JavaSourceFromString> classes = new HashSet<JavaSourceFromString>();

		logger.debug("ClassName: " + fullyQualifiedClassName
				+ "; classSource: " + classSource);
		JavaSourceFromString customFileObj = new JavaSourceFromString(
				fullyQualifiedClassName, classSource);

		classes.add(customFileObj);

		CompilationTask task = compiler.getTask(null, fileManager, null, null,
				null, classes);

		boolean result = task.call();

		if (logger.isDebugEnabled())
			logger.debug(new StringBuffer("Compilation of class ").append(
					fullyQualifiedClassName).append(
					result ? " succeeded" : " failed"));

		Map<String, ClassJavaFileObject> generatedClasses = fileManager
				.generatedClasses();

		try {
			fileManager.close();
		} catch (IOException e) {
			logger.error(e);
		}

		Class<?> clz;
		ByteClassLoader cLoader;
		cLoader = new ByteClassLoader(null, generatedClasses);
		try {
			clz = cLoader.loadClass(fullyQualifiedClassName);
		} catch (ClassNotFoundException e) {
			throw (new CompilationException(e));
		}

		try {
			logger.info("Method being called: " + classHandlerMethodToCall);
			Method method = clz.getMethod(classHandlerMethodToCall);
			logger.info("Found method? " + (method != null ? "yes" : "no"));
			method.invoke(null);
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// e.printStackTrace();
			logger.error(e);
			throw new CompilationException(e);
		}

	}

}
