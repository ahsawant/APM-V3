package com.escala.agent.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class MethodCalledClassModifier extends ClassVisitor {

	public MethodCalledClassModifier() {
		super(Opcodes.ASM5, new ClassWriter(ClassWriter.COMPUTE_FRAMES));
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {

		Type[] argumentTypes = Type.getArgumentTypes(desc);
		logArgumentTypes(argumentTypes);
		boolean staticMethod = (Opcodes.ACC_STATIC & access) != 0;
		return (new MethodCalledMethodModifier(name, staticMethod,
				argumentTypes, super.cv.visitMethod(access, name, desc,
						signature, exceptions)));
	}

	private void logArgumentTypes(Type[] argumentTypes) {
		for (int i = 0; i < argumentTypes.length; i++) {
			System.out.println("Arg" + i + " type: " + argumentTypes[i]);
		}
	}

	public ClassWriter getWriter() {
		return (ClassWriter) (super.cv);
	}

}
