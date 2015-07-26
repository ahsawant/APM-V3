package com.escala.agent.compiler;

import java.io.IOException;
import java.net.URI;

class SourceCustomJavaFileObject extends CustomJavaFileObject {

	private String classSource;

	public SourceCustomJavaFileObject(String binaryName, URI uri,
			String classSource) {
		super(binaryName, uri);
		this.classSource = classSource;
	}

	public Kind getKind() {
		return (Kind.SOURCE);
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		return classSource;
	}
}