package org.bool.lunch;

import java.util.Collection;

import org.apache.commons.lang3.reflect.MethodUtils;

public class Luncher implements Runnable {
	
	private final Class<?> cls;
	
	private final String[] args;
	
	public static Luncher create(String className, Collection<String> args) throws ClassNotFoundException {
		return create(className, args.toArray(new String[args.size()]));
	}
	
	public static Luncher create(String className, String... args) throws ClassNotFoundException {
		Class<?> cls = Class.forName(className);
		return create(cls, args);
	}
	
	public static Luncher create(Class<?> cls, String... args) {
 		return new Luncher(cls, args);
	}
	
	public Luncher(Class<?> cls, String[] args) {
		this.cls = cls;
		this.args = args;
	}

	@Override
	public void run() {
		try {
			MethodUtils.invokeStaticMethod(cls, "main", new Object[] {args}, new Class[] {String[].class});
		} catch (Exception e) {
			throw new RuntimeException("Main method invocation failed", e);
		}
	}
}
