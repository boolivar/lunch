package org.bool.lunch;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CachedRunnerFactory<T> {

	private final RunnerFactory delegate;
	
	private final Map<T, Runner> runners;
	
	private final Function<T, RunnerType> keyMapper;
	
	public static RunnerFactory cachedWrapper(RunnerFactory runnerFactory) {
		return new CachedRunnerFactory<>(runnerFactory, t -> t, new EnumMap<>(RunnerType.class))::lookup;
	}
	
	public CachedRunnerFactory(RunnerFactory delegate, Function<T, RunnerType> keyMapper) {
		this(delegate, keyMapper, new HashMap<>());
	}
	
	public CachedRunnerFactory(RunnerFactory delegate, Function<T, RunnerType> keyMapper, Map<T, Runner> runners) {
		this.delegate = delegate;
		this.keyMapper = keyMapper;
		this.runners = runners;
	}
	
	public Runner lookup(T type) {
		return runners.computeIfAbsent(type, keyMapper.andThen(delegate::create));
	}
}
