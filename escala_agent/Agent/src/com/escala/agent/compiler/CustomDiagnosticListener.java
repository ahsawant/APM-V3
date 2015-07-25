package com.escala.agent.compiler;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;

public class CustomDiagnosticListener implements
		DiagnosticListener<JavaFileObject> {

	@Override
	public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
	}

}
