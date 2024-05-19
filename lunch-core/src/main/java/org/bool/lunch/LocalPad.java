package org.bool.lunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LocalPad implements LaunchPad {

	private static final Logger log = LoggerFactory.getLogger(LocalPad.class);
	
	private final LunchBox lunchBox;

	public LocalPad(LunchBox lunchBox) {
		this.lunchBox = lunchBox;
	}
	
	@Override
	public void launch(Lunch lunch) {
		List<Process> processes = new ArrayList<>();
		try {
			CountDownLatch latch = new CountDownLatch(lunch.getItems().size());
			for (LunchItem item : lunch.getItems()) {
				Lunched lunched = lunchBox.launch(item, (what, exitCode) -> latch.countDown());
				log.info("Process {} for {} started", lunched.getProcess(), lunched.getLunchItem());
			}
			latch.await();
		} catch (Exception e) {
			log.error("Lunch terminated", e);
		} finally {
			processes.forEach(Process::destroy);
		}
	}
}
