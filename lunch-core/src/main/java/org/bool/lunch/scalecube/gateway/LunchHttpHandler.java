package org.bool.lunch.scalecube.gateway;

import org.reactivestreams.Publisher;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.QueryStringDecoder;
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
				.compose(response::sendObject)
				;
	}

	private ServiceMessage createMessage(HttpServerRequest request, ByteBuf buf) {
		QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
		Map<String, String> headers = decoder.parameters().entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().get(0)));
		return ServiceMessage.builder()
				.qualifier(decoder.path())
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
