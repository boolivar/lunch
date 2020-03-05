package org.bool.lunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProcessRunner implements Runner {
	
	private static final Logger log = LoggerFactory.getLogger(ProcessRunner.class);

	private Redirect redirectOutput = Redirect.INHERIT;
	
	private boolean redirectErrorStream = true;
	
	public void setRedirectOutput(Redirect redirectOutput) {
		this.redirectOutput = redirectOutput;
	}

	public void setRedirectErrorStream(boolean redirectErrorStream) {
		this.redirectErrorStream = redirectErrorStream;
	}
	
	@Override
	public Process run(String command, Collection<String> args) {
		ArrayList<String> commandArgs = new ArrayList<>();
		commandArgs.add(command);
		commandArgs.addAll(args);
		return run(commandArgs);
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
