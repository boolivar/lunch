package org.bool.lunch;

public class Lunched {

	private final LunchProcess process;
	
	private final LunchItem lunchItem;
	
	public Lunched(LunchProcess process, LunchItem lunchItem) {
		this.process = process;
		this.lunchItem = lunchItem;
	}

	public LunchProcess getProcess() {
		return process;
	}

	public LunchItem getLunchItem() {
		return lunchItem;
	}

	@Override
	public String toString() {
		return "Lunched [process=" + process + ", lunchItem=" + lunchItem + "]";
	}
}
