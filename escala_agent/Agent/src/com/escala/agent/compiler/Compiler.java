package com.escala.agent.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Compiler {

	public static void compile(String className, String classSource,
			ClassLoader cl) {

		String fileNameToCompile = java.io.File.separator + "tmp"
				+ java.io.File.separator + className;

		File fileToCompile = null;
		FileOutputStream fStream = null;
		try {
			fileToCompile = new File(fileNameToCompile);
			fStream = new FileOutputStream(fileToCompile);
			fStream.write(classSource.getBytes());
			fStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} finally {
			if (fStream != null)
				try {
					fStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		ClassLoader oldCl = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(cl);
		try {

			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

			StandardJavaFileManager standardJavaFileManager = compiler
					.getStandardFileManager(null, null, null);
			final JavaFileManager fileManager = new CustomClassloaderJavaFileManager(
					cl, standardJavaFileManager);

			CustomJavaFileObjectSource customFileObj = new CustomJavaFileObjectSource(
					className, fileToCompile.toURI(), classSource);
			Set<CustomJavaFileObjectSource> classes = new HashSet<CustomJavaFileObjectSource>();
			classes.add(customFileObj);

			CompilationTask task = compiler.getTask(null, fileManager, null,
					null, null, classes);
			boolean result = task.call();

			if (result) {
				System.out.println("Compilation is successful");
			} else {
				System.out.println("Compilation Failed");
			}
		} finally {
			Thread.currentThread().setContextClassLoader(oldCl);
		}
	}
}
