package org.bool.lunch;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureLunchProcess implements LunchProcess {

	private final String id;
	
	private final Future<Integer> future;

	public FutureLunchProcess(String id, Future<Integer> future) {
		this.id = id;
		this.future = future;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public Integer waitFor(Duration duration) throws InterruptedException {
		try {
			if (duration == null) {
				return future.get();
			}
			return future.get(duration.toMillis(), TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			return null;
		} catch (ExecutionException e) {
			return -1;
		}
	}

	@Override
	public void destroy() {
		future.cancel(false);
	}
}
