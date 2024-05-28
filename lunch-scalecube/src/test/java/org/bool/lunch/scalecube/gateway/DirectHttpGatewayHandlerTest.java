package org.bool.lunch.scalecube.gateway;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.scalecube.services.ServiceCall;
import io.scalecube.services.api.ServiceMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;
import reactor.test.StepVerifier;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class DirectHttpGatewayHandlerTest {

	@Mock
	private ServiceCall serviceCall;

	@InjectMocks
	private DirectHttpGatewayHandler handler;

	@Test
	void testHandler(@Mock HttpServerRequest request, @Mock HttpServerResponse response, @Mock NettyOutbound outbound) {
		var data = Unpooled.buffer();
		given(request.receive())
			.willReturn(ByteBufFlux.fromString(Mono.just("test")));
		given(request.path())
			.willReturn("test-path");
		given(request.requestHeaders())
			.willReturn(new DefaultHttpHeaders());

		given(response.responseHeaders())
			.willReturn(new DefaultHttpHeaders());
		given(serviceCall.requestOne(any()))
			.willReturn(Mono.just(ServiceMessage.builder().data(data).build()));
		given(response.sendObject(data))
			.willReturn(outbound);
		given(outbound.then())
			.willReturn(Mono.empty());

		StepVerifier.create(handler.handle(request, response))
			.verifyComplete();
	}
}
