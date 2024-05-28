package org.bool.lunch.scalecube.gateway;

import io.scalecube.net.Address;
import io.scalecube.services.ServiceCall;
import io.scalecube.services.gateway.Gateway;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class HttpGatewayTest {

	private final ServiceCall serviceCall = mock(ServiceCall.class);

	private final HttpGatewayServerFactory serverFactory = mock(HttpGatewayServerFactory.class);

	private final HttpGatewayOptions options = HttpGatewayOptions.builder()
			.host("host.test")
			.port(2000)
			.id("http-test")
			.serviceCall(serviceCall)
			.build();

	private final HttpGateway gateway = new HttpGateway(options, serverFactory);

	@Test
	void testConfig() {
		assertThat(gateway.id())
			.isEqualTo("http-test");
		assertThat(gateway.address())
			.returns(options.getHost(), Address::host)
			.returns(2000, Address::port)
			;
	}

	@Test
	void testStart() {
		var server = mock(DisposableServer.class);
		given(serverFactory.create(options.getHost(), 2000, serviceCall))
			.willReturn((Mono) Mono.just(server));
		given(server.host())
			.willReturn("www.test.org");
		given(server.port())
			.willReturn(3000);

		StepVerifier.create(gateway.start())
			.assertNext(gw -> assertThat(gw)
				.isNotSameAs(gateway)
				.extracting(Gateway::address)
					.returns(3000, Address::port)
					.returns("www.test.org", Address::host))
			.verifyComplete();
	}

	@Test
	void testStop() {
		var server = mock(DisposableServer.class);
		given(serverFactory.create(options.getHost(), 2000, serviceCall))
			.willReturn((Mono) Mono.just(server));
		given(server.onDispose())
			.willReturn(Mono.empty());

		StepVerifier.create(gateway.start().flatMap(Gateway::stop))
			.verifyComplete();

		then(server).should().dispose();
	}

	@Test
	void testEmptyStop() {
		StepVerifier.create(gateway.stop())
			.verifyComplete();
	}
}
