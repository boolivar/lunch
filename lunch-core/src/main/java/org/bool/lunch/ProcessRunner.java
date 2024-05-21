package org.bool.lunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ProcessRunner implements Runner {
	
	private static final Logger log = LoggerFactory.getLogger(ProcessRunner.class);

	private final Function<Process, String> pidReader;
	
	private final Redirect redirectOutput;
	
	private final boolean redirectErrorStream;
	
	public ProcessRunner() {
		this(PidReader.DEFAULT::processId, Redirect.INHERIT, true);
	}
	
	public ProcessRunner(Function<Process, String> pidReader, Redirect redirectOutput, boolean redirectErrorStream) {
		this.pidReader = pidReader;
		this.redirectOutput = redirectOutput;
		this.redirectErrorStream = redirectErrorStream;
	}
	
	@Override
	public NativeLunchProcess run(String command, Collection<String> args) {
		var commandArgs = new ArrayList<String>();
		commandArgs.add(command);
		commandArgs.addAll(args);
		Process process = run(commandArgs);
		return new NativeLunchProcess(pidReader.apply(process), process);
	}
	
	public Process run(List<String> args) {
		log.info("Start process with args: {}", args);
		ProcessBuilder builder = new ProcessBuilder(args);
		builder.redirectOutput(redirectOutput);
		builder.redirectErrorStream(redirectErrorStream);
		try {
			return builder.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
