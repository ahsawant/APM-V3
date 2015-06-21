package com.escala.system.staticm;

public class CalledMethodProxy {

	public static final CalledMethodProxy singleton = new CalledMethodProxy();

	private static volatile ICalledMethod listener;

	private CalledMethodProxy() {
	}

	public static void setListener(ICalledMethod listener) {
		CalledMethodProxy.listener = listener;
	}

	public static void calledMethod(Object targetObject, String methodName,
			Object[] args) {

		if (listener != null)
			listener.calledMethod(targetObject, methodName, args);
		else
			debugMethodCall(targetObject, methodName, args);
	}

	public static final void debugMethodCall(Object targetObject,
			String methodName, Object[] args) {
		System.out.println(getDebugString(targetObject, methodName, args));
	}

	public static final String getDebugString(Object targetObject,
			String methodName, Object[] args) {

		StringBuilder string = new StringBuilder();

		string.append("############################# " + args.length
				+ " Method Arguments for " + methodName + "():\n");

		if (targetObject != null)
			string.append("Object class:" + targetObject.getClass().getName()
					+ "; toString(): " + targetObject + "\n");
		for (int i = 0; i < args.length; i++) {
			if (args[i] != null) {
				if (!args[i].getClass().isArray())
					string.append("Arg[" + i + "]: "
							+ args[i].getClass().getName() + "; toString(): "
							+ args[i] + "\n");
				else {
					string.append("Arg[" + i + "]: {");
					printArrayObjects((Object[]) args[i], string);
					string.append("}\n");

				}
			} else
				string.append("Arg[" + i + "]: <NULL>\n");
		}
		return (string.toString());
	}

	private static void printArrayObjects(Object[] array, StringBuilder string) {
		String separator = "";
		for (int i = 0; i < array.length; i++) {
			if (array[i] != null) {
				if (!array[i].getClass().isArray())
					string.append(separator + array[i].toString());
				else {
					string.append(separator + "{");
					printArrayObjects((Object[]) array[i], string);
					string.append("}");
				}
			} else
				string.append(separator + "<NULL>");
			separator = ", ";
		}
	}

	public static void calledMethod() {
		System.out
				.println("############################# A instrumented static method was called!");
	}

	public void calledMemberMethod() {
		System.out
				.println("############################# A instrumented virtual method was called!");
	}

}
