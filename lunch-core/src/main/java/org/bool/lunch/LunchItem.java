package org.bool.lunch;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LunchItem {

	private String name;
	
	private String type;
	
	private String command;
	
	private List<String> args;
	
	public LunchItem() {
	}
	
	public LunchItem(String command) {
		this.command = command;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public List<String> getArgs() {
		return args;
	}

	public void setArgs(List<String> args) {
		this.args = args;
	}

	@Override
	public String toString() {
		return "LunchItem [name=" + name + ", type=" + type + ", command=" + command + ", args=" + args + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, type, command, args);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj != null && getClass() == obj.getClass()) {
			LunchItem other = (LunchItem) obj;
			return Arrays.asList(name, type, command, args)
					.equals(Arrays.asList(other.name, other.type, other.command, other.args));
		}
		return false;
	}
}

