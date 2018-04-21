package org.bool.lunch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LunchPad {

	private static final Logger log = LoggerFactory.getLogger(LunchPad.class);
	
	private final LunchRunner runner;

	public LunchPad(LunchRunner runner) {
		this.runner = runner;
	}
	
	public void launch(Lunch lunch) {
		List<Process> processes = new ArrayList<>();
		try {
			ExecutorService executor = Executors.newCachedThreadPool();
			try {
				CompletionService<Process> completion = new ExecutorCompletionService<>(executor);
				for (LunchItem item : lunch.getItems()) {
					Lunched lunched = runner.launch(item);
					if (lunched.getProcess() != null) {
						processes.add(lunched.getProcess());
						completion.submit(() -> await(lunched));
					}
					log.info("Process {} for {} started", lunched.getPid(), lunched.getLunchItem());
				}
				completion.take().get();
			} finally {
				executor.shutdown();
			}
		} catch (Exception e) {
			log.error("Lunch terminated", e);
		} finally {
			processes.forEach(Process::destroy);
		}
	}

	private Process await(Lunched lunched) throws InterruptedException {
		Process process = lunched.getProcess();
		int exitCode = process.waitFor();
		log.info("Exit code {} for process {}", exitCode, lunched.getPid());
		return process;
	}
}
