package org.bool.lunch.core;

import org.bool.lunch.LunchItem;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessBuilderMapper {

	public ProcessBuilder map(LunchItem item) {
		var builder = new ProcessBuilder(item.getCommand())
			.directory(item.getWorkDir())
			.redirectErrorStream(true)
			;
		if (item.getArgs() != null) {
			builder.command().addAll(item.getArgs());
		}
		if (item.getEnv() != null) {
			builder.environment().putAll(item.getEnv());
		}
		if (item.getLogFile() != null) {
			builder.redirectOutput(item.getLogFile());
		}
		return builder;
	}
}
