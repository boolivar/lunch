package org.bool.lunch;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class RunnerFactory {

	private final Map<String, Runner> runners;

	public RunnerFactory() {
		this(new HashMap<>());
	}
	
	public RunnerFactory(Map<String, Runner> runners) {
		this.runners = runners;
	}
	
	public Runner lookup(String type) {
		return runners.computeIfAbsent(type.trim().toUpperCase(), k -> create(RunnerType.valueOf(k)));
	}
	
	public Runner create(RunnerType type) {
		switch (type) {
		case JAVA:
			return createJavaRunner();
		case PROCESS:
			return createProcessRunner();
		case THREAD:
			return createThreadRunner();
		default:
			throw new IllegalArgumentException("Unexpected runner type: " + type);
		}
	}

	private ExecutorRunner createThreadRunner() {
		return new ExecutorRunner(Executors.newCachedThreadPool(), new ClassRunner());
	}
	
	private JavaProcessRunner createJavaRunner() {
		return new JavaProcessRunner(new ProcessRunner(), "java", classpath(), Collections.emptyList());
	}
	
	private ProcessRunner createProcessRunner() {
		return new ProcessRunner();
	}
	
	private String classpath() {
		return Arrays.stream(getClassLoader().getURLs())
				.map(URL::getFile).collect(Collectors.joining(File.pathSeparator));
	}
	
	private URLClassLoader getClassLoader() {
		ClassLoader classLoader = RunnerFactory.class.getClassLoader();
		if (classLoader instanceof URLClassLoader) {
			return (URLClassLoader) classLoader;
		}
		return (URLClassLoader) ClassLoader.getSystemClassLoader();
	}
}
