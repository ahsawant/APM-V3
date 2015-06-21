package com.escala.agent.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;

import com.escala.agent.log.ClassEntryWrapper.ModifiedClass;

public class LogClassTransformer implements ClassFileTransformer {

	private ClassEntries classEntries;

	private Map<String, Set<ClassLoader>> classLoaders = new Hashtable<String, Set<ClassLoader>>();

	// Define a static logger
	static final Logger logger = LogManager.getLogger(LogClassTransformer.class
			.getName());

	public LogClassTransformer(ClassEntries classEntries) {
		this.classEntries = classEntries;
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		byte[] byteArray = classfileBuffer;

		try {

			if (logger.isDebugEnabled())
				logger.debug("Transforming: " + className);
			trackClassLoaders(loader, className);

			// Only transform classes specified
			ModifiedClass modifiedClass = classEntries.get(className.replace(
					'/', '.'));
			if (modifiedClass == null) {
				logger.info("Not transforming " + className);
				return (classfileBuffer);
			}

			logger.error("Transforming: " + className);

			ClassReader cr = new ClassReader(classfileBuffer);
			LogMethodCalledClassModifier mccm = new LogMethodCalledClassModifier(
					modifiedClass);

			// Skips frames as we're using ClassWriter.COMPUTE_FRAMES
			cr.accept(mccm, ClassReader.SKIP_FRAMES);

			byteArray = mccm.getWriter().toByteArray();

			// writeClass(className, byteArray);

		} catch (Throwable t) {
			logger.error(t);
		}
		return (byteArray);
	}

	private void trackClassLoaders(ClassLoader loader, String className) {
		synchronized (classLoaders) {
			if (logger.isDebugEnabled())
				logger.debug("Found class " + className);
			Set<ClassLoader> loaders = classLoaders.get(className);
			if (loaders == null) {
				loaders = new HashSet<ClassLoader>();
				classLoaders.put(className, loaders);
			}
			loaders.add(loader);
		}
	}

	// Write out class for debugging purpose (will fail in Windows)
	void writeClass(String className, byte[] byteArray) {
		try {
			new FileOutputStream(new File("/tmp/" + className + ".class"))
					.write(byteArray);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<String, Set<ClassLoader>> getClassLoaders() {
		return classLoaders;
	}
}
