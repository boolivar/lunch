package org.bool.lunch;

import java.util.Collections;

public class LunchRunner {
	
	private final RunnerFactory runnerFactory;
	
	private final PidReader pidReader;

	public LunchRunner(RunnerFactory runnerFactory, PidReader pidReader) {
		this.runnerFactory = runnerFactory;
		this.pidReader = pidReader;
	}
	
	public Lunched launch(LunchItem item) {
		Process process = run(item);
		String pid = pidReader.processId(process);
		return new Lunched(pid, process, item);
	}
	
	private Process run(LunchItem item) {
		Runner runner = lookupRunner(item.getType());
		return runner.run(item.getCommand(), item.getArgs() != null ? item.getArgs() : Collections.emptyList());
	}
	
	private Runner lookupRunner(String type) {
		if (type == null) {
			type = RunnerType.JAVA.name();
		}
		return runnerFactory.create(type);
	}
}
