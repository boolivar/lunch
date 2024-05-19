package org.bool.lunch;

import java.time.Duration;

public interface LunchProcess {

	Duration INFINITE_WAIT = null;

	default boolean isAlive() {
		return exitCode() == null;
	}

	default Integer exitCode() {
		return waitFor(Duration.ZERO);
	}

	default Integer waitFor() {
		return waitFor(INFINITE_WAIT);
	}

	String getPid();

	Integer waitFor(Duration duration);

	void destroy();
}
