package org.bool.lunch.scalecube.gateway;

import org.reactivestreams.Publisher;

import java.util.function.BiFunction;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.scalecube.services.ServiceCall;
import io.scalecube.services.api.ServiceMessage;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class ServiceCallHttpHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
	
	private final ServiceCall service;
	
	public ServiceCallHttpHandler(ServiceCall service) {
		this.service = service;
	}

	@Override
	public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
				.defaultIfEmpty(Unpooled.EMPTY_BUFFER)
				.map(buf -> createMessage(request, buf))
				.flatMap(service::requestOne)
				.flatMap(this::readMessage)
				.compose(response::sendObject);
	}

	private ServiceMessage createMessage(HttpServerRequest request, ByteBuf buf) {
		return ServiceMessage.builder()
				.qualifier(request.path())
				.data(buf)
				.build();
	}

	private Mono<?> readMessage(ServiceMessage message) {
		return Mono.just(message)
				.filter(ServiceMessage::hasData)
				.map(ServiceMessage::data);
	}
}
