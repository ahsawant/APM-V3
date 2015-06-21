package com.escala.agent.asm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;

public class SufixClassTransformer implements ClassFileTransformer {

	// Define a static logger
	static final Logger logger = LogManager
			.getLogger(SufixClassTransformer.class.getName());

	private String suffix;

	public SufixClassTransformer(String suffix) {
		this.suffix = suffix;
		logger.error("ClassTransformer loaded!");
	}

	public byte[] transform(ClassLoader loader, String className,
			Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
			byte[] classfileBuffer) throws IllegalClassFormatException {

		logger.info("Transforming " + className);

		// Only transform classes that end with the name passed in as className
		if (!className.endsWith(suffix))
			return (classfileBuffer);

		ClassReader cr = new ClassReader(classfileBuffer);
		MethodCalledClassModifier mccm = new MethodCalledClassModifier();

		// Skips frames as we're using ClassWriter.COMPUTE_FRAMES
		cr.accept(mccm, ClassReader.SKIP_FRAMES);

		byte[] byteArray = mccm.getWriter().toByteArray();

		// writeClass(className, byteArray);

		return (byteArray);
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
}
