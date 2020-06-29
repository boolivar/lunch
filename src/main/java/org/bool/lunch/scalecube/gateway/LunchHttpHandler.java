package org.bool.lunch.scalecube.gateway;

import org.reactivestreams.Publisher;

import java.util.function.BiConsumer;
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
	
	private final BiConsumer<ServiceMessage, ByteBuf> messageEncoder;

	public LunchHttpHandler(Function<ServiceMessage, Mono<ServiceMessage>> messageProcessor, BiConsumer<ServiceMessage, ByteBuf> messageEncoder) {
		this.messageProcessor = messageProcessor;
		this.messageEncoder = messageEncoder;
	}

	@Override
	public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
				.defaultIfEmpty(Unpooled.EMPTY_BUFFER)
				.map(buf -> createMessage(request, buf))
				.flatMap(messageProcessor)
				.flatMap(message -> encodeResponse(response, message))
				.compose(response::sendObject)
				;
	}

	private ServiceMessage createMessage(HttpServerRequest request, ByteBuf buf) {
		return ServiceMessage.builder()
				.qualifier(request.uri())
				.data(buf)
				.build();
	}

	private Mono<ByteBuf> encodeResponse(HttpServerResponse response, ServiceMessage message) {
		if (!message.hasData()) {
			return Mono.empty();
		}
		if (message.hasData(ByteBuf.class)) {
			return Mono.just(message.data());
		}
		
		ByteBuf buffer = response.alloc().buffer();
		try {
			messageEncoder.accept(message, buffer);
		} catch (Exception e) {
			buffer.release();
			throw e;
		}
		return Mono.just(buffer);
	}
}
