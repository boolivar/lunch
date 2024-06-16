package org.bool.lunch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LunchItem {

	private String name;

	private String type;

	private String command;

	private List<String> args;

	private Map<String, String> env;

	private File workDir;

	private File logFile;

	private List<LunchItem> items;
}
