package org.bool.lunch.scalecube.gateway;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.scalecube.services.ServiceCall;
import io.scalecube.services.ServiceReference;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.services.registry.api.ServiceRegistry;
import reactor.core.publisher.Mono;

public class LunchMessageProcessor implements Function<ServiceMessage, Mono<ServiceMessage>> {

	private final ServiceCall serviceCall;
	
	private final ServiceRegistry serviceRegistry;
	
	public LunchMessageProcessor(ServiceCall serviceCall, ServiceRegistry serviceRegistry) {
		this.serviceCall = serviceCall;
		this.serviceRegistry = serviceRegistry;
	}
	
	@Override
	public Mono<ServiceMessage> apply(ServiceMessage message) {
		String scale = message.header("scale");
		
		if (scale == null) {
			return serviceCall.requestOne(message);
		}
		
		return Mono.just(scale)
				.map(Integer::valueOf).onErrorReturn(Integer.MAX_VALUE)
				.flatMap(s -> requestMany(message, s));
	}
	
	private Mono<ServiceMessage> requestMany(ServiceMessage message, Integer scale) {
		List<ServiceReference> refs = serviceRegistry.lookupService(message);

		if (refs.isEmpty()) {
			return Mono.empty();
		}
		
		List<Mono<ServiceMessage>> monos = refs.stream()
				.limit(scale)
				.map(ref -> requestOne(ref, message))
				.collect(Collectors.toList());
		
		return Mono.zip(monos, this::buildMessage);
	}
	
	private Mono<ServiceMessage> requestOne(ServiceReference ref, ServiceMessage message) {
		return serviceCall.requestOne(message, null, ref.address())
				.onErrorResume(t -> Mono.just(buildMessage(t.getMessage())));
	}
	
	private ServiceMessage buildMessage(Object data) {
		return ServiceMessage.builder().data(data).build();
	}
}
