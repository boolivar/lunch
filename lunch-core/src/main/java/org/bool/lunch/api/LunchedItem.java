package org.bool.lunch.api;

import reactor.core.publisher.Mono;

public interface LunchedItem {

	String getName();

	String getPid();

	Mono<Integer> exitCode();

	Mono<Void> terminate(boolean force);

}