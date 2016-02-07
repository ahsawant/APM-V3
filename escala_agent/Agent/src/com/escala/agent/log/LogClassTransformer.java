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

		// Skip even analyzing classes loaded by the agent classloader (this
		// will avoid startup deadlocks)
		if (loader == LogClassTransformer.class.getClassLoader()) {
			// System.out.println("Classloader not tracked: " + loader
			// + "; Class: " + className);
			return (classfileBuffer);
		}

		byte[] byteArray = classfileBuffer;

		try {

			if (logger.isDebugEnabled())
				logger.debug("Transforming: " + className);

			// Unclear why null classNames go by
			if (className == null) {
				logger.info("Not transforming class as name is null!");
				return (classfileBuffer);
			}

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

		} catch (Throwable t) {
			logger.error(t);
		}

		// Write original class
		writeClass(className + ".orig", classfileBuffer);

		// Write new class
		// writeClass(className + ".new" , byteArray);

		return (byteArray);
	}

	private void trackClassLoaders(ClassLoader loader, String className) {

		synchronized (classLoaders) {
			if (logger.isDebugEnabled())
				logger.debug("Found class " + className);

			if (loader == null) {
				// Using the system class loader as "proxy" in the hopes the
				// delegation to load later on will yield this class (theory to
				// be tested). And yes, theoritically a class could be defined
				// by both, but I don't think that happens in practice
				loader = ClassLoader.getSystemClassLoader();
			}

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
			File targetFile = new File("/tmp/" + className + ".class");
			File parent = targetFile.getParentFile();
			if (!parent.exists() && !parent.mkdirs()) {
				throw new IllegalStateException("Couldn't create dir: "
						+ parent);
			}
			new FileOutputStream(targetFile).write(byteArray);
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
