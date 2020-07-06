package org.bool.lunch;

import java.util.Collections;

public class LunchRunner {
	
	private final RunnerFactory runnerFactory;
	
	public LunchRunner(RunnerFactory runnerFactory) {
		this.runnerFactory = runnerFactory;
	}
	
	public Lunched launch(LunchItem item) {
		LunchProcess process = run(item);
		return new Lunched(process, item);
	}
	
	private LunchProcess run(LunchItem item) {
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
