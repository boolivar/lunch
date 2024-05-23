package org.bool.lunch.api;

import org.bool.lunch.LunchItem;

import reactor.core.publisher.Mono;

public interface Luncher {

	Mono<? extends LunchedItem> launch(LunchItem item);

}