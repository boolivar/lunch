package org.bool.lunch.scalecube.gateway;

import org.reactivestreams.Publisher;

import java.util.function.BiFunction;
import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.scalecube.services.api.ServiceMessage;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class LunchHttpHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
	
	private final Function<ServiceMessage, Mono<ServiceMessage>> messageProcessor;

	public LunchHttpHandler(Function<ServiceMessage, Mono<ServiceMessage>> messageProcessor) {
		this.messageProcessor = messageProcessor;
	}

	@Override
	public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
				.defaultIfEmpty(Unpooled.EMPTY_BUFFER)
				.map(buf -> createMessage(request, buf))
				.flatMap(messageProcessor)
				.flatMap(this::readMessage)
				.compose(response::sendObject);
	}

	private ServiceMessage createMessage(HttpServerRequest request, ByteBuf buf) {
		return ServiceMessage.builder()
				.qualifier(request.uri())
				.data(buf)
				.build();
	}

	private Mono<?> readMessage(ServiceMessage message) {
		return Mono.just(message)
				.filter(ServiceMessage::hasData)
				.map(ServiceMessage::data);
	}
}
