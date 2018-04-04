package org.bool.lunch;

import java.util.ArrayList;
import java.util.Collection;

public class JavaProcessRunner implements Runner {

	private final Runner processRunner;
	
	private final String javaCommand;
	
	private final String classPath;

	private final Collection<String> javaArgs;

	public JavaProcessRunner(Runner processRunner, String javaCommand, String classPath, Collection<String> javaArgs) {
		this.processRunner = processRunner;
		this.javaCommand = javaCommand;
		this.classPath = classPath;
		this.javaArgs = javaArgs;
	}
	
	public Process run(Class<?> cls, Collection<String> args) {
		return run(cls.getName(), args);
	}

	@Override
	public Process run(String className, Collection<String> args) {
		ArrayList<String> commandArgs = new ArrayList<>();
		commandArgs.addAll(javaArgs);
		commandArgs.add("-cp");
		commandArgs.add(classPath);
		commandArgs.add(className);
		commandArgs.addAll(args);
		return processRunner.run(javaCommand, commandArgs);
	}
}
