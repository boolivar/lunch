package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.Lunched;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class LocalLunchService implements LunchService {

	private final Luncher luncher;

	private final Map<String, Lunched> lunchedMap;
	
	public LocalLunchService(Luncher luncher) {
		this(luncher, new ConcurrentHashMap<>());
	}
	
	public LocalLunchService(Luncher luncher, Map<String, Lunched> lunchedMap) {
		this.luncher = luncher;
		this.lunchedMap = lunchedMap;
	}

	@Override
	public Flux<Lunched> launch(LunchItem item) {
		return luncher.launch(item)
				.doOnNext(lunched -> lunchedMap.put(lunched.getPid(), lunched))
				.cache()
				;
	}

	@Override
	public Mono<Lunched> land(String pid) {
		return Mono.justOrEmpty(lunchedMap.get(pid))
				.doOnNext(lunched -> lunched.getProcess().destroy())
				;
	}

	@Override
	public Mono<List<Stat>> stats() {
		return Mono.just(lunchedMap.values())
				.map(values -> values.stream().map(this::buildStat).collect(Collectors.toList()))
				.cache()
				;
	}

	private Stat buildStat(Lunched lunched) {
		return new Stat(lunched.getPid(), lunched.getLunchItem(),
				lunched.getProcess().isAlive() ? null : lunched.getProcess().exitValue());
	}
}