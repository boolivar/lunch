package org.bool.lunch.api;

import reactor.core.publisher.Mono;

import java.time.Duration;

public interface LunchedItem {

	String getName();

	String getPid();

	Mono<Integer> exitCode();

	Mono<Void> terminate(boolean force);

	default boolean isAlive() {
		return !exitCode().toFuture().isDone();
	}

	default Object getInfo() {
		return getName() + ": " + getPid();
	}

	default Mono<Void> terminate(Duration timeout) {
		return terminate(false)
			.timeout(timeout).onErrorResume(e -> Mono.just(e).log().and(terminate(true)));
	}
}