package org.bool.lunch.scalecube;

import org.apache.commons.lang3.RandomUtils;
import org.bool.lunch.scalecube.gateway.LunchHttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;

import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class LunchHttpServerTest implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {
	
	@Mock
	Consumer<String> consumer;
	
	int port = RandomUtils.nextInt(49152, 65535);
	
	HttpClient client = HttpClient.create().port(port);
	
	DisposableServer server = new LunchHttpServer("localhost", port, this).bind().block();
	
	@AfterAll
	void shutdownServer() {
		server.dispose();
	}

	@Override
	public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
				.map(bb -> bb.toString(StandardCharsets.UTF_8)).doOnNext(consumer)
				.thenReturn("out").compose(response::sendString);
	}
	
	@Test
	void testRequest() {
		String response = client.post()
			.send(Mono.just(Unpooled.copiedBuffer("in", StandardCharsets.UTF_8)))
			.response((rsp, byteBuf) -> {
				assertEquals(HttpResponseStatus.OK, rsp.status());
				return byteBuf;
			})
			.map(bb -> bb.retain())
			.reduce(Unpooled::wrappedBuffer)
			.map(bb -> bb.toString(StandardCharsets.UTF_8))
			.block();
		
		assertEquals("out", response);
		then(consumer)
				.should().accept("in");
	}
}
