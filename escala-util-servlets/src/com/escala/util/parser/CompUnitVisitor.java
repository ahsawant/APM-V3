package com.escala.util.parser;

import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

import com.escala.xfer.parser.ClassInfo;
import com.escala.xfer.parser.JavaFile;
import com.escala.xfer.parser.MethodInfo;

public class CompUnitVisitor extends VoidVisitorAdapter<Object> {

	String classPackage = "";
	

	@Override
	public void visit(ClassOrInterfaceDeclaration classDecl, Object javaFile) {
		// Only process classes. Interfaces cannot be instrumented
		if (!classDecl.isInterface()) {
			String className = "";
			if (classPackage != "") {
				className = classPackage + "." + classDecl.getName();
			} else {
				// If there is no package information
				className = classDecl.getName();
			}
			((JavaFile)javaFile).addClass(new ClassInfo(className));
			System.out.println("Found class " + className);
		}
		super.visit(classDecl, javaFile);
	}

	@Override
	public void visit(MethodDeclaration method, Object javaFile) {
		String methodName = method.getName();
		MethodInfo methodInfo = new MethodInfo(methodName);
		System.out.println("Found method " + methodName + "(");
		if (method.getParameters() != null) {
			for (Parameter param : method.getParameters()) {
				String paramType = param.getType().toString();
				//String paramName = param.getId().getName();
				methodInfo.addParam(paramType); //, paramName);
				System.out.println(paramType); // + " " + paramName);
			}
		}
		System.out.println(")");
		List<ClassInfo> classes = ((JavaFile)javaFile).getClasses();
		if (classes.size() > 0) {
			ClassInfo myClass = classes.get(classes.size() - 1);
			myClass.addMethod(methodInfo);
		}
		super.visit(method, javaFile);
	}

	@Override
	public void visit(PackageDeclaration pack, Object arg1) {
		classPackage = pack.getName().toString();
		System.out.println("Found package " + classPackage);
		super.visit(pack, arg1);
	}

}
