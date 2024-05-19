package org.bool.lunch;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class NativeLunchProcess implements LunchProcess {

	private final String pid;
	
	private final Process process;
	
	public NativeLunchProcess(String pid, Process process) {
		this.pid = pid;
		this.process = process;
	}
	
	@Override
	public String getPid() {
		return pid;
	}

	@Override
	public Integer waitFor(Duration duration) {
		try {
			if (duration == INFINITE_WAIT) {
				return process.waitFor();
			}
			return process.waitFor(duration.toMillis(), TimeUnit.MILLISECONDS)
					? process.exitValue()
					: null;
		} catch (InterruptedException e) {
			throw ExceptionUtils.asRuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		process.destroy();
	}
}
