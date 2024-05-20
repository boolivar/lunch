package org.bool.lunch.scalecube;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class LunchHttpServerTest implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {

	@Mock
	private Consumer<String> consumer;

	private final DisposableServer server = new LunchHttpServer("localhost", 0, this).bind().blockOptional().orElseThrow();

	private final HttpClient client = HttpClient.create().port(server.port());

	@AfterAll
	void shutdownServer() {
		server.dispose();
	}

	@Override
	public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.asString(StandardCharsets.UTF_8)
			.doOnNext(consumer)
			.thenReturn("out")
			.transform(response::sendString)
			.then();
	}

	@Test
	void testRequest() {
		String response = client.post()
			.send(Mono.just(Unpooled.copiedBuffer("in", StandardCharsets.UTF_8)))
			.responseSingle((rsp, bb) -> rsp.status() == HttpResponseStatus.OK ? bb.asString() : Mono.error(new IllegalStateException("Response status: " + rsp.status())))
			.block();

		assertThat(response)
			.isEqualTo("out");
		then(consumer)
			.should().accept("in");
	}
}
