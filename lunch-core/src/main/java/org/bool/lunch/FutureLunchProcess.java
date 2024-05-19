package org.bool.lunch;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.Duration;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class FutureLunchProcess implements LunchProcess {

	private final String pid;
	
	private final Future<Integer> future;

	public FutureLunchProcess(String pid, Future<Integer> future) {
		this.pid = pid;
		this.future = future;
	}
	
	@Override
	public String getPid() {
		return pid;
	}

	@Override
	public Integer waitFor(Duration duration) {
		try {
			if (duration == null) {
				return future.get();
			}
			return future.get(duration.toMillis(), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw ExceptionUtils.asRuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		future.cancel(false);
	}
}
