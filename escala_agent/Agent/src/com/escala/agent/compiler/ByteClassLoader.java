package com.escala.agent.compiler;

import java.util.HashMap;
import java.util.Map;

public class ByteClassLoader extends ClassLoader {

	private final Map<String, ClassJavaFileObject> extraClassDefs;

	public ByteClassLoader(ClassLoader parent) {
		super(parent);
		this.extraClassDefs = null;
	}

	public ByteClassLoader(ClassLoader parent,
			Map<String, ClassJavaFileObject> extraClassDefs) {
		super(parent);
		this.extraClassDefs = new HashMap<String, ClassJavaFileObject>(
				extraClassDefs);
	}

	@Override
	protected Class<?> findClass(final String name)
			throws ClassNotFoundException {
		ClassJavaFileObject fileObject = this.extraClassDefs.get(name);
		if (fileObject != null) {
			byte[] classBytes = fileObject.getByteArray();
			return defineClass(name, classBytes, 0, classBytes.length);
		}
		return super.findClass(name);
	}
}
