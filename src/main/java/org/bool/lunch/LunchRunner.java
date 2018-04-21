package org.bool.lunch;

import java.util.function.Function;

public class LunchRunner {
	
	private final Function<String, Runner> mapper;
	
	private final PidReader pidReader;

	public LunchRunner(Function<String, Runner> mapper, PidReader pidReader) {
		this.mapper = mapper;
		this.pidReader = pidReader;
	}
	
	public Lunched launch(LunchItem item) {
		Process process = run(item);
		String pid = pidReader.processId(process);
		return new Lunched(pid, process, item);
	}
	
	private Process run(LunchItem item) {
		Runner runner = lookupRunner(item.getType());
		return runner.run(item.getCommand(), item.getArgs());
	}
	
	private Runner lookupRunner(String type) {
		if (type == null) {
			type = RunnerType.JAVA.name();
		}
		return mapper.apply(type.toUpperCase());
	}
}
