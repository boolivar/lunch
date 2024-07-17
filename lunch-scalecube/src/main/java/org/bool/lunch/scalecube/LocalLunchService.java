package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.api.LunchedItem;
import org.bool.lunch.api.Luncher;

import lombok.AllArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class LocalLunchService implements LunchService {

	private final Luncher luncher;

	private final Map<String, LunchedItem> lunchedMap;
	
	public LocalLunchService(Luncher luncher) {
		this(luncher, new ConcurrentHashMap<>());
	}

	@Override
	public Mono<LunchInfo> launch(LunchItem item) {
		return luncher.launch(item)
				.doOnNext(lunched -> lunchedMap.put(lunched.getPid(), lunched))
				.flatMap(this::buildInfo)
				;
	}

	@Override
	public Mono<LunchInfo> land(String uid) {
		return Mono.justOrEmpty(lunchedMap.get(uid))
				.doOnNext(lunched -> lunched.terminate(Duration.ofSeconds(5)))
				.flatMap(this::buildInfo)
				;
	}

	@Override
	public Mono<List<LunchInfo>> stats() {
		return Flux.fromIterable(lunchedMap.values())
				.flatMap(this::buildInfo)
				.collectList()
				;
	}

	private Mono<LunchInfo> buildInfo(LunchedItem lunched) {
		return (lunched.isAlive() ? Mono.<Integer>empty() : lunched.exitCode()).materialize()
			.map(signal -> new LunchInfo(lunched.getPid(), lunched.getName(), Objects.toString(lunched.getInfo(), null), signal.get()));
	}
}