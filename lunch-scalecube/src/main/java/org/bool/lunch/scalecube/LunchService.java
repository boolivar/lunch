package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;

import io.scalecube.services.annotations.Service;
import io.scalecube.services.annotations.ServiceMethod;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface LunchService {

	@ServiceMethod
	Mono<LunchInfo> launch(LunchItem item);

	@ServiceMethod
	Mono<LunchInfo> land(String pid);

	@ServiceMethod
	Mono<List<LunchInfo>> stats();
}