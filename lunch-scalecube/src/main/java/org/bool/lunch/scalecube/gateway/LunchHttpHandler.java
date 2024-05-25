package org.bool.lunch.scalecube.gateway;

import org.reactivestreams.Publisher;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.services.transport.api.DataCodec;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class LunchHttpHandler implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
	
	private final Function<ServiceMessage, Mono<ServiceMessage>> messageProcessor;
	
	private final DataCodec dataCodec;

	public LunchHttpHandler(Function<ServiceMessage, Mono<ServiceMessage>> messageProcessor, DataCodec dataCodec) {
		this.messageProcessor = messageProcessor;
		this.dataCodec = dataCodec;
	}

	@Override
	public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
				.defaultIfEmpty(Unpooled.EMPTY_BUFFER)
				.map(buf -> createMessage(request, buf))
				.flatMap(messageProcessor)
				.flatMap(message -> encodeResponse(response, message))
				.transform(response::sendObject)
				;
	}

	private ServiceMessage createMessage(HttpServerRequest request, ByteBuf buf) {
		var headers = Stream.concat(request.params() != null ? request.params().entrySet().stream() : Stream.of(),
				request.requestHeaders().entries().stream())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));
		return ServiceMessage.builder()
				.qualifier(request.path())
				.headers(headers)
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
			dataCodec.encode(new ByteBufOutputStream(buffer), message.data());
		} catch (Exception e) {
			buffer.release();
			throw new RuntimeException("Error encode data from message: " + message, e);
		}
		return Mono.just(buffer);
	}
}
