package org.bool.lunch;

import java.util.Collection;
import java.util.concurrent.Executor;

public class ExecutorRunner implements Runner {

	private final Executor executor;
	
	private final Runner runner;

	public ExecutorRunner(Executor executor, Runner runner) {
		this.executor = executor;
		this.runner = runner;
	}

	@Override
	public LunchProcess run(String command, Collection<String> args) {
		executor.execute(() -> runner.run(command, args));
		return null;
	}
}
