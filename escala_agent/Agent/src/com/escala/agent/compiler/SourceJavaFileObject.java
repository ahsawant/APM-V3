package com.escala.agent.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class SourceJavaFileObject implements JavaFileObject {

	// Define a static logger
	static final Logger logger = LogManager
			.getLogger(SourceJavaFileObject.class.getName());
	private String classSource;
	private String binaryName;

	public SourceJavaFileObject(String binaryName, String classSource) {
		this.binaryName = binaryName;
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

	@Override
	public URI toUri() {
		throw new UnsupportedOperationException();

	}

	@Override
	public String getName() {
		return (binaryName);
	}

	@Override
	public InputStream openInputStream() throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public OutputStream openOutputStream() throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public Writer openWriter() throws IOException {
		throw new UnsupportedOperationException();

	}

	@Override
	public long getLastModified() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean delete() {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isNameCompatible(String simpleName, Kind kind) {
		String baseName = simpleName + kind.extension;
		return kind.equals(getKind())
				&& (baseName.equals(getName()) || getName().endsWith(
						"/" + baseName));
	}

	@Override
	public NestingKind getNestingKind() {
		throw new UnsupportedOperationException();

	}

	@Override
	public Modifier getAccessLevel() {
		throw new UnsupportedOperationException();

	}
}