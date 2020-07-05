package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;

import java.util.List;

import io.scalecube.services.annotations.Service;
import io.scalecube.services.annotations.ServiceMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface LunchService {

	@ServiceMethod
	Flux<LunchInfo> launch(LunchItem item);

	@ServiceMethod
	Mono<LunchInfo> land(String pid);

	@ServiceMethod
	Mono<List<LunchInfo>> stats();
}