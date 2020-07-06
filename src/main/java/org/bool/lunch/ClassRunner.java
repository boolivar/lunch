package org.bool.lunch;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class ClassRunner {
	
	private static final Logger log = LoggerFactory.getLogger(ClassRunner.class);

	private final String method;
	
	public ClassRunner() {
		this("main");
	}
	
	public ClassRunner(String method) {
		this.method = method;
	}
	
	public int run(String className, Collection<String> args) {
		run(className, method, args);
		return 0;
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
		log.info("Run class {}, method {}, args {}", cls, method, args);
		return MethodUtils.invokeStaticMethod(cls, method, new Object[] { args }, new Class[] { String[].class });
	}
}
