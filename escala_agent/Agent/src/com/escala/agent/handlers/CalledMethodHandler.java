package com.escala.agent.handlers;

import com.escala.agent.log.CalledMethod;
import com.escala.system.staticm.ICalledMethod;

public class CalledMethodHandler implements ICalledMethod {

	@Override
	public void calledMethod(Object targetObject, String methodName,
			Object[] args) {
		// CalledMethodProxy.debugMethodCall(targetObject, methodName, args);

		CalledMethod.statCalledMethod(targetObject, methodName, args);
	}

}
