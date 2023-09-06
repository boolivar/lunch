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
	public Flux<LunchInfo> launch(LunchItem item) {
		return luncher.launch(item)
				.doOnNext(lunched -> lunchedMap.put(lunched.getProcess().getPid(), lunched))
				.map(this::buildInfo)
				.cache()
				;
	}

	@Override
	public Mono<LunchInfo> land(String uid) {
		return Mono.justOrEmpty(lunchedMap.get(uid))
				.doOnNext(lunched -> lunched.getProcess().destroy())
				.map(this::buildInfo)
				;
	}

	@Override
	public Mono<List<LunchInfo>> stats() {
		return Mono.just(lunchedMap.values())
				.map(values -> values.stream().map(this::buildInfo).collect(Collectors.toList()))
				.cache()
				;
	}

	private LunchInfo buildInfo(Lunched lunched) {
		return new LunchInfo(lunched.getProcess().getPid(), lunched.getProcess().exitCode(), lunched.getLunchItem());
	}
}