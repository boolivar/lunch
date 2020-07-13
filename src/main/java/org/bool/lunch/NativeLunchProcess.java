package org.bool.lunch;

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
	public Integer waitFor(Duration duration) throws InterruptedException {
		if (duration == INFINITE_WAIT) {
			return process.waitFor();
		}
		return process.waitFor(duration.toMillis(), TimeUnit.MILLISECONDS)
				? process.exitValue()
				: null;
	}

	@Override
	public void destroy() {
		process.destroy();
	}
}
