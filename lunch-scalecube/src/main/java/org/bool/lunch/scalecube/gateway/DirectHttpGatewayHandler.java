package org.bool.lunch.scalecube.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.scalecube.services.ServiceCall;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.services.transport.api.DataCodec;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class DirectHttpGatewayHandler {

	private final ServiceCall serviceCall;

	private final DataCodec dataCodec;

	public DirectHttpGatewayHandler(ServiceCall serviceCall) {
		this(serviceCall, HttpHeaderValues.APPLICATION_JSON.toString());
	}

	public DirectHttpGatewayHandler(ServiceCall serviceCall, String contentType) {
		this(serviceCall, DataCodec.getInstance(contentType));
	}

	public Publisher<Void> handle(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.defaultIfEmpty(Unpooled.EMPTY_BUFFER)
			.map(buf -> createMessage(request, buf))
			.flatMap(serviceCall::requestOne)
			.flatMap(message -> sendResponse(response, message))
			;
	}

	private ServiceMessage createMessage(HttpServerRequest request, ByteBuf buf) {
		var requestParams = request.params();
		var headers = Stream.concat(
				requestParams != null ? requestParams.entrySet().stream() : Stream.of(),
				request.requestHeaders().entries().stream()
			).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b));
		return ServiceMessage.builder()
			.qualifier(request.path())
			.headers(headers)
			.data(buf.retain())
			.build();
	}

	@SneakyThrows
	private Mono<Void> sendResponse(HttpServerResponse response, ServiceMessage message) {
		if (!message.hasData()) {
			return response.send();
		}
		if (message.hasData(ByteBuf.class)) {
			response.responseHeaders().set(HttpHeaderNames.CONTENT_TYPE, message.dataFormatOrDefault());
			return response.sendObject((ByteBuf) message.data()).then();
		}
		try (ByteBufOutputStream out = new ByteBufOutputStream(response.alloc().buffer())) {
			response.responseHeaders().set(HttpHeaderNames.CONTENT_TYPE, dataCodec.contentType());
			dataCodec.encode(out, message.data());
			return response.sendObject(out.buffer().retain()).then();
		}
	}
}
