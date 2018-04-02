package org.bool.lunch;

import java.util.List;
import java.util.Objects;

public class LunchItem {
	
	private String className;
	
	private List<String> args;
	
	public LunchItem() {
	}
	
	public LunchItem(String className) {
		this.className = className;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}
	
	@Override
	public String toString() {
		return "LunchItem [className=" + className + ", args=" + args + "]";
	}
	
	@Override
	public int hashCode() {
		int result = 31 + Objects.hashCode(args);
		return 31 * result + Objects.hashCode(className);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && getClass() == obj.getClass()) {
			LunchItem other = (LunchItem) obj;
			return Objects.equals(className, other.className) && Objects.equals(args, other.args);
		}
		return false;
	}
}

