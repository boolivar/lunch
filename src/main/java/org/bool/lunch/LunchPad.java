package org.bool.lunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LunchPad {

	private static final Logger log = LoggerFactory.getLogger(LunchPad.class);
	
	private final LunchBox lunchBox;

	public LunchPad(LunchBox lunchBox) {
		this.lunchBox = lunchBox;
	}
	
	public void launch(Lunch lunch) {
		List<Process> processes = new ArrayList<>();
		try {
			BlockingQueue<Lunched> queue = new LinkedBlockingQueue<>();
			for (LunchItem item : lunch.getItems()) {
				Lunched lunched = lunchBox.launch(item, (what, exitCode) -> queue.offer(what));
				log.info("Process {} for {} started", lunched.getPid(), lunched.getLunchItem());
			}
			queue.take();
		} catch (Exception e) {
			log.error("Lunch terminated", e);
		} finally {
			processes.forEach(Process::destroy);
		}
	}
}
