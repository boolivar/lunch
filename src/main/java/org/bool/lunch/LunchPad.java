package org.bool.lunch;

import java.util.function.Function;

import org.bool.jpid.PidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LunchPad {

	private static final Logger log = LoggerFactory.getLogger(LunchPad.class);
	
	private final Function<String, Runner> mapper;

	public LunchPad(Function<String, Runner> mapper) {
		this.mapper = mapper;
	}
	
	public void launch(Lunch lunch) {
		for (LunchItem item : lunch.getItems()) {
			launch(item);
		}
	}

	public void launch(LunchItem item) {
		Runner runner = lookupRunner(item.getType());
		Process process = runner.run(item.getCommand(), item.getArgs());
		if (log.isInfoEnabled()) {
			log.info("Process {} started for {} with arguments: {}", processId(process), item.getCommand(), item.getArgs());
		}
	}
	
	private String processId(Process process) {
		if (process == null) {
			return "this(" + PidUtils.getPid() + ")";
		}
		try {
			return String.valueOf(PidUtils.getPid(process));
		} catch (Exception e) {
			log.warn("Error reading processId", e);
		}
		return "unknown";
	}
	
	private Runner lookupRunner(String type) {
		if (type == null) {
			type = RunnerType.JAVA.name();
		}
		return mapper.apply(type.toUpperCase());
	}
}
