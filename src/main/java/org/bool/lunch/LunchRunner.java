package org.bool.lunch;

import java.util.function.Function;

public class LunchRunner {
	
	private final Function<String, Runner> mapper;

	public LunchRunner(Function<String, Runner> mapper) {
		this.mapper = mapper;
	}
	
	public Process lunch(LunchItem item) {
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
