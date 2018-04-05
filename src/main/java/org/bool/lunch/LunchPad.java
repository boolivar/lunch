package org.bool.lunch;

import java.util.function.Function;

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
		runner.run(item.getCommand(), item.getArgs());
		log.info("Started {} with arguments: {}", item.getCommand(), item.getArgs());
	}
	
	private Runner lookupRunner(String type) {
		return mapper.apply(type);
	}
}
