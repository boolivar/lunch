package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;

import java.io.Serializable;

public class Stat implements Serializable {

	private static final long serialVersionUID = 1L;

	private String pid;

	private LunchItem lunch;

	private Integer exitCode;

	public Stat() {
	}

	public Stat(String pid, LunchItem lunch, Integer exitCode) {
		this.pid = pid;
		this.lunch = lunch;
		this.exitCode = exitCode;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public LunchItem getLunch() {
		return lunch;
	}

	public void setLunch(LunchItem lunch) {
		this.lunch = lunch;
	}

	public Integer getExitCode() {
		return exitCode;
	}

	public void setExitCode(Integer exitCode) {
		this.exitCode = exitCode;
	}

	@Override
	public String toString() {
		return "Stat [pid=" + pid + ", lunch=" + lunch + ", exitCode=" + exitCode + "]";
	}
}
