package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;

public class LunchInfo {

	private final String pid;
	
	private final Integer exitCode;
	
	private final LunchItem lunchItem;
	
	public LunchInfo(String pid, Integer exitCode, LunchItem lunchItem) {
		this.pid = pid;
		this.exitCode = exitCode;
		this.lunchItem = lunchItem;
	}

	public String getPid() {
		return pid;
	}
	
	public Integer getExitCode() {
		return exitCode;
	}

	public LunchItem getLunchItem() {
		return lunchItem;
	}
}
