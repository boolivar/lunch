package org.bool.lunch;

import java.util.Collection;

import org.apache.commons.lang3.reflect.MethodUtils;

public class ClassRunner implements Runner {

	private final String method;
	
	public ClassRunner() {
		this("main");
	}
	
	public ClassRunner(String method) {
		this.method = method;
	}
	
	@Override
	public Process run(String className, Collection<String> args) {
		run(className, method, args);
		return null;
	}
	
	public Object runClass(String className, Collection<String> args) {
		return run(className, method, args);
	}
	
	public static Object run(String className, String methodName, Collection<String> args) {
		try {
			Class<?> cls = Class.forName(className);
			return run(cls, methodName, args);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Runner error, className: " + className + ", args: " + args, e);
		}
	}
	
	public static Object run(Class<?> cls, String method, Collection<String> args) throws ReflectiveOperationException {
		return MethodUtils.invokeStaticMethod(cls, method, new Object[] { args }, new Class[] { String[].class });
	}
}
