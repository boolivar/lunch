package org.bool.lunch;

import java.time.Duration;

public interface LunchProcess {

	Duration INFINITE_WAIT = null;

	default boolean isAlive() {
		return exitCode() == null;
	}
	
	default Integer exitCode() {
		try {
			return waitFor(Duration.ZERO);
		} catch (InterruptedException e) {
			throw new RuntimeException("Unexpected exception", e);
		}
	}
	
	default Integer waitFor() throws InterruptedException {
		return waitFor(INFINITE_WAIT);
	}
	
	String getPid();
	
	Integer waitFor(Duration duration) throws InterruptedException;
	
	void destroy();
}
