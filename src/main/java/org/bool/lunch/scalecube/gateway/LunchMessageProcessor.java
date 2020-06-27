package org.bool.lunch.scalecube.gateway;

import java.util.function.Function;

import io.scalecube.services.ServiceCall;
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
		return serviceCall.requestOne(message);
	}
}
