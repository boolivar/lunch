package org.bool.lunch;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.ToIntBiFunction;

public class ExecutorRunner implements Runner {
	
	private static final String PID = String.valueOf(ProcessHandle.current().pid());
	
	private final ExecutorService executor;
	
	private final ToIntBiFunction<String, Collection<String>> handler;
	
	public ExecutorRunner(ExecutorService executor, ToIntBiFunction<String, Collection<String>> handler) {
		this.executor = executor;
		this.handler = handler;
	}

	@Override
	public LunchProcess run(String command, Collection<String> args) {
		Future<Integer> future = executor.submit(() -> handler.applyAsInt(command, args));
		return new FutureLunchProcess(PID, future);
	}
}
