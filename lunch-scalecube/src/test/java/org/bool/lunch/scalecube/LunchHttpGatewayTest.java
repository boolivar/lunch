package org.bool.lunch.scalecube;

import io.scalecube.net.Address;
import io.scalecube.services.gateway.Gateway;
import io.scalecube.services.gateway.GatewayOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import org.bool.lunch.scalecube.gateway.LunchHttpGateway;
import org.bool.lunch.scalecube.gateway.LunchHttpServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LunchHttpGatewayTest {
	
	private final LunchHttpServer server = mock(LunchHttpServer.class);

	private final LunchHttpGateway gateway = new LunchHttpGateway(new GatewayOptions().id("testId"), server);

	@Test
	void testGetters() {
		Address address = Address.create("host", 1234);
		given(server.getAddress())
			.willReturn(address);

		assertThat(gateway)
			.returns("testId", Gateway::id)
			.returns(address, Gateway::address)
			;
	}
	
	@Test
	void testStart() {
		Address address = Address.create("host", 1234);
		given(server.getAddress())
			.willReturn(address);
		given(server.bind())
			.willReturn(Mono.just(mock()));

		Gateway active = gateway.start().block();
		assertThat(active)
			.isNotSameAs(gateway)
			.returns("testId", Gateway::id)
			.returns(address, Gateway::address)
			;

		assertThat(active.stop())
			.isNotNull();
		assertThat(gateway.stop())
			.isNotNull();
		then(server.bind().block()).should(never()).dispose();
	}
	
	@Test
	void testStop() {
		given(server.bind())
			.willReturn(Mono.just(mock()));
		assertThat(gateway.start().block().stop().block())
			.isNull();
		then(server.bind().block()).should().dispose();
	}
}
