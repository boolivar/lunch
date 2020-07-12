package org.bool.lunch;

public class Lunched {
	
	private final String uid;

	private final LunchProcess process;
	
	private final LunchItem lunchItem;
	
	public Lunched(String uid, LunchProcess process, LunchItem lunchItem) {
		this.uid = uid;
		this.process = process;
		this.lunchItem = lunchItem;
	}

	public String getUid() {
		return uid;
	}

	public LunchProcess getProcess() {
		return process;
	}

	public LunchItem getLunchItem() {
		return lunchItem;
	}
	
	@Override
	public String toString() {
		return "Lunched [uid=" + uid + ", process=" + process + ", lunchItem=" + lunchItem + "]";
	}
}
