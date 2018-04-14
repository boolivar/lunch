package org.bool.lunch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
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
		List<Process> processes = new ArrayList<>();
		try {
			CompletionService<Process> completion = new ExecutorCompletionService<>(Executors.newCachedThreadPool());
			for (LunchItem item : lunch.getItems()) {
				Process process = launch(item);
				if (process != null) {
					processes.add(process);
					completion.submit(() -> await(process));
				}
			}
			completion.take().get();
		} catch (Exception e) {
			log.error("Lunch terminated", e);
		} finally {
			processes.forEach(Process::destroy);
		}
	}

	public Process launch(LunchItem item) {
		Runner runner = lookupRunner(item.getType());
		Process process = runner.run(item.getCommand(), item.getArgs());
		if (log.isInfoEnabled()) {
			log.info("Process {} started for {} with arguments: {}", processId(process), item.getCommand(), item.getArgs());
		}
		return process;
	}
	
	private Process await(Process process) throws InterruptedException {
		int exitCode = process.waitFor();
		log.info("Exit code {} for process {}", exitCode, process);
		return process;
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
