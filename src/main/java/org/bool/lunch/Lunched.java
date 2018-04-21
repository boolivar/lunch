package org.bool.lunch;

public class Lunched {

	private final String pid;
	
	private final Process process;
	
	private final LunchItem lunchItem;
	
	public Lunched(String pid, Process process, LunchItem lunchItem) {
		this.pid = pid;
		this.process = process;
		this.lunchItem = lunchItem;
	}

	public String getPid() {
		return pid;
	}

	public Process getProcess() {
		return process;
	}

	public LunchItem getLunchItem() {
		return lunchItem;
	}
}
