package org.bool.lunch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PidReader {

	public static final PidReader DEFAULT = new PidReader();

	private static final Logger log = LoggerFactory.getLogger(PidReader.class);

	public String processId(Process process) {
		if (process == null) {
			return "this(" + ProcessHandle.current().pid() + ")";
		}
		try {
			return String.valueOf(process.pid());
		} catch (Exception e) {
			log.warn("Error reading processId", e);
		}
		return "unknown";
	}
}
