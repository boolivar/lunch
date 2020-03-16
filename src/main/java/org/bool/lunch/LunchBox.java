package org.bool.lunch;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntConsumer;

public class LunchBox {
	
	private final LunchRunner lunchRunner;
	
	private final ExecutorService executorService;
	
	public LunchBox(LunchRunner lunchRunner) {
		this(lunchRunner, Executors.newCachedThreadPool(new BasicThreadFactory.Builder()
				.namingPattern("LunchBox-thread-d").build()));
	}
	
	public LunchBox(LunchRunner lunchRunner, ExecutorService executorService) {
		this.lunchRunner = lunchRunner;
		this.executorService = executorService;
	}
	
	public Lunched launch(LunchItem item, IntConsumer onDestroy) {
		Lunched lunched = lunchRunner.launch(item);
		
		if (lunched.getProcess() != null) {
			executorService.submit(() -> {
				int exitCode = lunched.getProcess().waitFor();
				onDestroy.accept(exitCode);
				return exitCode;
			});
		}
		
		return lunched;
	}
}
