package org.bool.lunch;

import org.bool.jpid.LongValueAccessor;
import org.bool.jpid.PidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class PidReader {
	
	public static final PidReader DEFAULT = new PidReader(PidUtils.cache(new ConcurrentHashMap<>()));
	
	private static final Logger log = LoggerFactory.getLogger(PidReader.class);
	
	private final LongValueAccessor valueAccessor;
	
	public PidReader(LongValueAccessor valueAccessor) {
		this.valueAccessor = valueAccessor;
	}
	
	public String processId(Process process) {
		if (process == null) {
			return "this(" + PidUtils.getPid() + ")";
		}
		try {
			return String.valueOf(valueAccessor.getValue(process));
		} catch (Exception e) {
			log.warn("Error reading processId", e);
		}
		return "unknown";
	}
}
