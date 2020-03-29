package org.bool.lunch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class JavaProcessRunner implements Runner {

	private final Runner processRunner;
	
	private final String javaCommand;
	
	private final String classPath;

	public JavaProcessRunner(Runner processRunner, String javaCommand, String classPath) {
		this.processRunner = processRunner;
		this.javaCommand = javaCommand;
		this.classPath = classPath;
	}
	
	public Process run(Class<?> cls, Collection<String> args, Collection<String> javaArgs) {
		return run(cls.getSimpleName() + " " + args.stream().collect(Collectors.joining(" ")), javaArgs);
	}

	@Override
	public Process run(String command, Collection<String> javaArgs) {
		ArrayList<String> commandArgs = new ArrayList<>();
		commandArgs.addAll(javaArgs);
		commandArgs.add("-cp");
		commandArgs.add(classPath);
		commandArgs.addAll(Arrays.asList(command.split(" +")));
		return processRunner.run(javaCommand, commandArgs);
	}
}
