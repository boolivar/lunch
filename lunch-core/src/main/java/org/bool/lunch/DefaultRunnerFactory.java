package org.bool.lunch;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.File;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultRunnerFactory implements RunnerFactory {

	@Override
	public Runner create(String type) {
		switch (type) {
		case "JAVA":
			return createJavaRunner();
		case "PROCESS":
			return createProcessRunner();
		case "THREAD":
			return createThreadRunner();
		default:
			return instantiateRunner(type);
		}
	}

	private ExecutorRunner createThreadRunner() {
		return new ExecutorRunner(Executors.newCachedThreadPool(), new ClassRunner()::run);
	}

	private JavaProcessRunner createJavaRunner() {
		return new JavaProcessRunner(new ProcessRunner(), "java", classpath(Thread.currentThread().getContextClassLoader()));
	}

	private ProcessRunner createProcessRunner() {
		return new ProcessRunner();
	}
	
	private Runner instantiateRunner(String className) {
		try {
			return (Runner) Class.forName(className).getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Error creating runner " + className, e);
		}
	}

	private String classpath(ClassLoader cl) {
		return Stream.of(retrieveClaspath(cl)).map(URL::getFile).collect(Collectors.joining(File.pathSeparator));
	}

	private URL[] retrieveClaspath(ClassLoader cl) {
		try {
			Object ucp = FieldUtils.readField(cl, "ucp", true);
			return (URL[]) MethodUtils.invokeMethod(ucp, true, "getURLs");
		} catch (Exception e) {
			throw new RuntimeException("Error read urls from classLoader: " + cl, e);
		}
	}
}
