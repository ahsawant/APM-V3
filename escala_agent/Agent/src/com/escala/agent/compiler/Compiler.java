package com.escala.agent.compiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
	public static void compile(String className, String classSource,
			ClassLoader cl) throws CompilationException {
		try {
			compile(className, classSource, cl,
					Files.createTempDirectory("compiler_"));
		} catch (IOException e) {
			logger.debug(e);
			throw new CompilationException(e);
		}
	}

	/*
	 * Compiles the java class given by fullyQualifiedClassName, with source
	 * classSource and using the classloader cl to resolve dependencies during
	 * compile time. The folder name provided by temporaryFolder will be used as
	 * temporary local storage on disk.
	 */
	public static void compile(String fullyQualifiedClassName,
			String classSource, ClassLoader cl, Path tmpDir)
			throws CompilationException {

		/*
		 * File fileToCompile = null; FileOutputStream fStream = null; try {
		 * fileToCompile = new File(tmpDir.toFile(), fullyQualifiedClassName);
		 * fStream = new FileOutputStream(fileToCompile);
		 * fStream.write(classSource.getBytes()); fStream.close(); } catch
		 * (IOException e) { logger.debug(e); throw new CompilationException(e);
		 * } finally { if (fStream != null) try { fStream.close(); } catch
		 * (IOException e) { // Logged as error in case this is the first (and
		 * thus // final) error logger.error(e); // Not going to throw for an
		 * error closing the stream } }
		 */

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager standardJavaFileManager = compiler
				.getStandardFileManager(null, null, null);
		final JavaFileManager fileManager = new CustomClassloaderJavaFileManager(
				cl, standardJavaFileManager);

		SourceCustomJavaFileObject customFileObj = new SourceCustomJavaFileObject(
				fullyQualifiedClassName, classSource);
		Set<SourceCustomJavaFileObject> classes = new HashSet<SourceCustomJavaFileObject>();
		classes.add(customFileObj);

		CompilationTask task = compiler.getTask(null, fileManager, null, null,
				null, classes);
		boolean result = task.call();

		if (result) {
			System.out.println("Compilation is successful");
		} else {
			System.out.println("Compilation Failed");
		}

	}
}
