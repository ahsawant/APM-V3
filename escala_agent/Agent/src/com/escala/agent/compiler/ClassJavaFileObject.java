package com.escala.agent.compiler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.SimpleJavaFileObject;

class ClassJavaFileObject extends SimpleJavaFileObject {
	private final String binaryName;
	private final URI uri;
	private final String name;
	private ByteArrayOutputStream byteArray;

	public ClassJavaFileObject(String binaryName, URI uri) {
		super(uri, Kind.CLASS);
		this.uri = uri;
		this.binaryName = binaryName;
		name = uri.getPath() == null ? uri.getSchemeSpecificPart() : uri
				.getPath(); // for FS based URI the path is not null, for JAR
							// URI the scheme specific part is not null
	}

	@Override
	public URI toUri() {
		return uri;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return uri.toURL().openStream(); // easy way to handle any URI!
	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		byteArray = new ByteArrayOutputStream();
		return (byteArray);
	}

	public byte[] getByteArray() {
		if (byteArray == null)
			throw (new IllegalStateException());
		return (byteArray.toByteArray());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean delete() {
		throw new UnsupportedOperationException();
	}

	@Override
	public NestingKind getNestingKind() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Modifier getAccessLevel() {
		throw new UnsupportedOperationException();
	}

	public String binaryName() {
		return binaryName;
	}

	@Override
	public String toString() {
		return "CustomJavaFileObject{" + "uri=" + uri + '}';
	}
}