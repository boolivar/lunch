package org.bool.lunch.core;

import org.bool.lunch.LunchItem;
import org.bool.lunch.api.Luncher;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.Objects;

@AllArgsConstructor
public class LocalProcessLuncher implements Luncher {

	private final ProcessBuilderMapper mapper;

	@Override
	public Mono<LocalProcessLunched> launch(LunchItem item) {
		return Mono.just(item)
			.map(mapper::map)
			.flatMap(builder -> Mono.fromCallable(builder::start))
			.map(process -> new LocalProcessLunched(Objects.toString(item.getName(), item.getCommand()), process));
	}
}
