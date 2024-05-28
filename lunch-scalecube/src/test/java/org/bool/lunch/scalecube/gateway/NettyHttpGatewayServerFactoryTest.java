package org.bool.lunch.scalecube.gateway;

import io.scalecube.services.ServiceCall;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.test.StepVerifier;

import java.util.function.BiFunction;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class NettyHttpGatewayServerFactoryTest implements BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> {

	@Mock
	private HttpGatewayHandlerFactory handlerFactory;

	@InjectMocks
	private NettyHttpGatewayServerFactory serverFactory;

	@Test
	void testServer(@Mock ServiceCall serviceCall) {
		given(handlerFactory.create(serviceCall))
			.willReturn((BiFunction) this);

		StepVerifier.create(serverFactory.create("localhost", 0, serviceCall)
				.flatMap(server -> HttpClient.create().get().uri("localhost:" + server.port()).responseContent().aggregate().asString()))
			.expectNext("Test ok")
			.verifyComplete();
	}

	@Override
	public Publisher<Void> apply(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate().then(response.sendString(Mono.just("Test ok")).then());
	}
}
