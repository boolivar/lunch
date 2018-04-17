package org.bool.lunch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bool.jpid.PidUtils;
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
					Process process = runner.lunch(item);
					String pid = processId(process);
					if (process != null) {
						processes.add(process);
						completion.submit(() -> await(pid, process));
					}
					log.info("Process {} for {} started", pid);
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

	private Process await(String pid, Process process) throws InterruptedException {
		int exitCode = process.waitFor();
		log.info("Exit code {} for process {}", exitCode, pid);
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
}
