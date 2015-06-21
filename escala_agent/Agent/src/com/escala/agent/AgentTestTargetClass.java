package com.escala.agent;
public class AgentTestTargetClass {

	public AgentTestTargetClass() {
//		System.out.println("New AgentTestTargetClass instance.");
	}

	public static void fooLong(long l) {
//		System.out.println("Long value: " + l);

	}

	public static void fooDouble(double d) {
//		System.out.println("Double value: " + d);

	}

	public static void foo(int i, long l, float f, double d, String str) {
//		System.out.println("foo() was called the " + i + "th time!");
//		(new AgentTestTargetClass()).fooInstance(i, l, f, d, str);
//		System.out.println("Random stuff" + i);

	}

	public void fooInstance(int i, long l, float f, double d, String str) {
//		int testVariable = 5;
//		System.out.println("fooInstance() was called the " + i + "th time!");
//		System.out.println("TestVar: " + testVariable);
	}
}
