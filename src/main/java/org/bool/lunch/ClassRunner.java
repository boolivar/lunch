package org.bool.lunch;

import java.util.Collection;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.reflect.MethodUtils;

public class ClassRunner implements Runner {

	private final Executor executor;
	
	private final String method;
	
	public ClassRunner() {
		this(Runnable::run);
	}
	
	public ClassRunner(Executor executor) {
		this(executor, "main");
	}
	
	public ClassRunner(Executor executor, String method) {
		this.executor = executor;
		this.method = method;
	}
	
	@Override
	public Process run(String className, Collection<String> args) {
		executor.execute(() -> run(className, method, args));
		return null;
	}
	
	public Object run(String className, String methodName, Collection<String> args) {
		try {
			Class<?> cls = Class.forName(className);
			return run(cls, methodName, args);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Runner error, className: " + className + ", args: " + args, e);
		}
	}
	
	public Object run(Class<?> cls, String method, Collection<String> args) throws ReflectiveOperationException {
		return MethodUtils.invokeStaticMethod(cls, method, new Object[] { args }, new Class[] { String[].class });
	}
}
