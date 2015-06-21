package com.escala.agent.asm;

import java.lang.reflect.Method;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.escala.system.staticm.CalledMethodProxy;

public class MethodCalledMethodModifier extends MethodVisitor {

	private static final String STATIC_METHOD_NAME = "calledMethod";
	private static final String FIELD_NAME = "singleton";
	private static final String MEMBER_METHOD_NAME = "calledMemberMethod";

	private static final String INT_DESC = Type.INT_TYPE.getDescriptor();
	private static final String LONG_DESC = Type.LONG_TYPE.getDescriptor();
	private static final String FLOAT_DESC = Type.FLOAT_TYPE.getDescriptor();
	private static final String DOUBLE_DESC = Type.DOUBLE_TYPE.getDescriptor();

	private static Method pcm = null;
	private static Method pcmwa = null;
	private String methodName;
	private Type[] argumentTypes; // This does NOT include the "this" argument!
	private boolean staticMethod = false;
	{
		try {
			pcm = CalledMethodProxy.class.getMethod(STATIC_METHOD_NAME);
			pcmwa = CalledMethodProxy.class.getMethod(STATIC_METHOD_NAME,
					new Class[] { Object.class, String.class, Object[].class });
			CalledMethodProxy.class.getMethod(MEMBER_METHOD_NAME);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public MethodCalledMethodModifier(String methodName, boolean staticMethod,
			Type[] argumentTypes, MethodVisitor mv) {
		super(Opcodes.ASM5, mv);
		this.argumentTypes = argumentTypes;
		this.methodName = methodName;
		this.staticMethod = staticMethod;
		if (argumentTypes.length == 0) {
		}
	}

	@Override
	public void visitCode() {

		// injectCallsToPrintAccess();
		injectArgumentLogging();

		super.visitCode();
	}

	private final void injectPrimitiveArguments(Class<?> claz, int loadOpcode,
			Type primitiveType, int location) {
		System.out.println("Injected for argument of type '"
				+ primitiveType.getDescriptor() + "'");
		super.visitTypeInsn(Opcodes.NEW, Type.getInternalName(claz));
		// Duplicate so that the constructor can consume one copy
		super.visitInsn(Opcodes.DUP);
		super.visitVarInsn(loadOpcode, location);
		super.visitMethodInsn(Opcodes.INVOKESPECIAL,
				Type.getInternalName(claz), "<init>",
				Type.getMethodDescriptor(Type.VOID_TYPE, primitiveType));
	}

	/**
	 * Keep in mind when looking at this method, that non static methods have
	 * one additional argument that is NOT covered in the argumentTypes array
	 * (the "this")
	 */
	private void injectArgumentLogging() {

		System.out.println("injectArgumentLogging() begin: " + methodName);

		// Instantiate a new Object[# of arguments], note that this will left on
		// the operand stack after this instruction
		super.visitIntInsn(Opcodes.SIPUSH, argumentTypes.length);
		super.visitTypeInsn(Opcodes.ANEWARRAY, Type.getType(Object.class)
				.getInternalName());

		int stackSlot = -1;
		if (!staticMethod)
			stackSlot++; // Skip "this" for instance methods
		for (int i = 0; i < argumentTypes.length; i++) {
			stackSlot++;

			// First push a copy of the array to operand stack
			super.visitInsn(Opcodes.DUP);

			// Now push array index
			super.visitIntInsn(Opcodes.SIPUSH, i);

			// Now figure the type of the argument, convert to a subclass of
			// Object, and leave it on top of stack
			String descriptor = argumentTypes[i].getDescriptor();
			// If argument is a basic type
			if (descriptor.length() == 1) {
				// ..box it and leave it on stack
				if (descriptor.equals(INT_DESC)) {
					injectPrimitiveArguments(Integer.class, Opcodes.ILOAD,
							Type.INT_TYPE, stackSlot);
				} else if (descriptor.equals(LONG_DESC)) {
					injectPrimitiveArguments(Long.class, Opcodes.LLOAD,
							Type.LONG_TYPE, stackSlot);
					stackSlot++; // Increment twice to account for long
				} else if (descriptor.equals(FLOAT_DESC)) {
					injectPrimitiveArguments(Float.class, Opcodes.FLOAD,
							Type.FLOAT_TYPE, stackSlot);
				} else if (descriptor.equals(DOUBLE_DESC)) {
					injectPrimitiveArguments(Double.class, Opcodes.DLOAD,
							Type.DOUBLE_TYPE, stackSlot);
					stackSlot++; // Increment twice to account for double
				} else { // Oops, unknown type; push null and continue
					System.err
							.println("Unknown method arg type: " + descriptor);
					super.visitInsn(Opcodes.ACONST_NULL);
				}
			} else if (descriptor.startsWith("L")) {
				super.visitVarInsn(Opcodes.ALOAD, stackSlot);
			} else if (descriptor.startsWith("[")) {
				// push null to stack for now as we're not handling arrays
				super.visitInsn(Opcodes.ACONST_NULL);
			} else {
				// Oops, unknown type; push null and continue
				System.err.println("Unknown method arg type: " + descriptor);
				super.visitInsn(Opcodes.ACONST_NULL);
			}

			// Finally insert the object into the array
			super.visitInsn(Opcodes.AASTORE);
		}

		// Remember that we have the Object[] on the operand stack

		// Push "this" to stack if instance method and not a constructor
		if (staticMethod || methodName.equals("<init>"))
			super.visitInsn(Opcodes.ACONST_NULL);
		else
			super.visitVarInsn(Opcodes.ALOAD, 0);

		// Swap stack elements as "this" is first argument
		super.visitInsn(Opcodes.SWAP);

		// Push the methodName to stack
		super.visitLdcInsn(methodName);

		// Swap stack elements as methodName is second argument
		super.visitInsn(Opcodes.SWAP);

		// Call the static method to pass the Object[]
		Type pcmType = Type.getType(CalledMethodProxy.class);
		super.visitMethodInsn(Opcodes.INVOKESTATIC, pcmType.getInternalName(),
				STATIC_METHOD_NAME, Type.getMethodDescriptor(pcmwa));

	}

	private void injectCallsToPrintAccess() {

		// Call static method
		Type pcmType = Type.getType(CalledMethodProxy.class);

		// Best to always call through super class which delegates to mv so that
		// if it were doing something else, that would continue to work
		super.visitMethodInsn(Opcodes.INVOKESTATIC, pcmType.getInternalName(),
				STATIC_METHOD_NAME, Type.getMethodDescriptor(pcm));

		// Call member method

		Type pcmMemberType = Type.getType(CalledMethodProxy.class);

		String fieldDescriptor = null;
		try {
			fieldDescriptor = Type.getDescriptor(CalledMethodProxy.class
					.getField(FIELD_NAME).getDeclaringClass());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		System.out.println("Method descriptor: "
				+ Type.getMethodDescriptor(pcm));
		System.out.println("Field descriptor: " + fieldDescriptor);

		// It's best to call this through the SuperClass which delegates to mv
		// // so that if it were doing something else, that would continue to
		// work // as well.)
		super.visitFieldInsn(Opcodes.GETSTATIC,
				pcmMemberType.getInternalName(), FIELD_NAME, fieldDescriptor);
		super.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
				pcmMemberType.getInternalName(), MEMBER_METHOD_NAME,
				Type.getMethodDescriptor(pcm));

	}

}
