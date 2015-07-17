package com.escala.agent;

public class AgentTestMain {

	public static void main(final String args[]) throws Exception {

		for (int i = 0; i < 10; i++) {
			AgentTestTargetClass.fooLong(i);
			AgentTestTargetClass.fooDouble(i * 1.1);
			AgentTestTargetClass.foo(i, i, (float) (i * 1.1), i * 1.1, "str_"
					+ i);
		}
		
		System.out.println("Sleeping to wait for retransform.");
		Thread.sleep(30000);
		System.out.println("Done waiting for retransform.");

		for (int i = 0; i < 10; i++) {
			AgentTestTargetClass.fooLong(i);
			AgentTestTargetClass.fooDouble(i * 1.1);
			AgentTestTargetClass.foo(i, i, (float) (i * 1.1), i * 1.1, "str_"
					+ i);
		}
	}
}
