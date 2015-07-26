package com.escala.agent.compiler;

import java.util.HashSet;
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

	/*
	 * Compiles the java class given by className, with source classSource and
	 * using the classloader cl to resolve dependencies during compile time. The
	 * /tmp folder will be used as temporary storage on disk.
	 */
	public static void compile(String fullyQualifiedClassName,
			String classSource, ClassLoader cl) throws CompilationException {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager standardJavaFileManager = compiler
				.getStandardFileManager(null, null, null);
		final JavaFileManager fileManager = new CustomClassloaderJavaFileManager(
				cl, standardJavaFileManager);

		SourceJavaFileObject customFileObj = new SourceJavaFileObject(
				fullyQualifiedClassName, classSource);
		Set<SourceJavaFileObject> classes = new HashSet<SourceJavaFileObject>();
		classes.add(customFileObj);

		CompilationTask task = compiler.getTask(null, fileManager, null, null,
				null, classes);
		boolean result = task.call();

		if (logger.isDebugEnabled())
			logger.debug(new StringBuffer("Compilation of class ").append(
					fullyQualifiedClassName).append(
					result ? " succeeded" : " failed"));

	}
}
