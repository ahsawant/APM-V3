package com.escala.agent.compiler;

import java.io.IOException;
import java.net.URI;

class ClassCustomJavaFileObject extends CustomJavaFileObject {

	public ClassCustomJavaFileObject(String binaryName, URI uri) {
		super(binaryName, uri);
	}

	@Override
	public Kind getKind() {
		return (Kind.CLASS);
	}

	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		throw new UnsupportedOperationException();
	}

}