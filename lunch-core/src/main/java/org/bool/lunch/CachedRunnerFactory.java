package org.bool.lunch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedRunnerFactory implements RunnerFactory {

	private final RunnerFactory delegate;
	
	private final Map<String, Runner> cache;
	
	public CachedRunnerFactory(RunnerFactory delegate) {
		this(delegate, new ConcurrentHashMap<>());
	}
	
	public CachedRunnerFactory(RunnerFactory delegate, Map<String, Runner> cache) {
		this.delegate = delegate;
		this.cache = cache;
	}
	
	@Override
	public Runner create(String type) {
		return cache.computeIfAbsent(type, delegate::create);
	}
}
