package org.bool.lunch;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
		return new JavaProcessRunner(new ProcessRunner(), "java", classpath());
	}
	
	private ProcessRunner createProcessRunner() {
		return new ProcessRunner();
	}
	
	private Runner instantiateRunner(String className) {
		try {
			return (Runner) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Error creating runner " + className, e);
		}
	}
	
	private String classpath() {
		return Arrays.stream(getClassLoader().getURLs())
				.map(URL::getFile).collect(Collectors.joining(File.pathSeparator));
	}
	
	private URLClassLoader getClassLoader() {
		ClassLoader classLoader = DefaultRunnerFactory.class.getClassLoader();
		if (classLoader instanceof URLClassLoader) {
			return (URLClassLoader) classLoader;
		}
		return (URLClassLoader) ClassLoader.getSystemClassLoader();
	}
}
