package org.bool.lunch;

import java.util.HashMap;
import java.util.Map;

import org.bool.jpid.LongValueAccessor;
import org.bool.jpid.PidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PidReader {
	
	public static final PidReader DEFAULT = new PidReader(new HashMap<>());
	
	private static final Logger log = LoggerFactory.getLogger(PidReader.class);
	
	private final Map<Class<? extends Process>, LongValueAccessor> cache;
	
	public PidReader(Map<Class<? extends Process>, LongValueAccessor> cache) {
		this.cache = cache;
	}
	
	public String processId(Process process) {
		if (process == null) {
			return "this(" + PidUtils.getPid() + ")";
		}
		try {
			LongValueAccessor accessor = cache.computeIfAbsent(process.getClass(), PidUtils::getPidAccessor);
			return String.valueOf(accessor.getValue(process));
		} catch (Exception e) {
			log.warn("Error reading processId", e);
		}
		return "unknown";
	}
}
