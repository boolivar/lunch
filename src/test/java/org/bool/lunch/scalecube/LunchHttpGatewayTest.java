package org.bool.lunch.scalecube;

import org.bool.lunch.scalecube.gateway.LunchHttpGateway;
import org.junit.jupiter.api.Test;

import io.scalecube.net.Address;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.*;

class LunchHttpGatewayTest {
	
	DisposableServer server = mock(DisposableServer.class);

	LunchHttpGateway gateway = new LunchHttpGateway("test-gateway", Address.from("test:1234"), Mono.just(server));

	@Test
	void testNoInteractions() {
		assertEquals("test-gateway", gateway.id());
		assertEquals("test", gateway.address().host());
		assertEquals(1234, gateway.address().port());
		
		gateway.start();
		gateway.stop();
		
		then(server)
				.shouldHaveNoInteractions();
	}
	
	@Test
	void testStart() {
		assertSame(gateway, gateway.start().block());
	}
	
	@Test
	void testStop() {
		gateway.stop().block();
		
		then(server)
				.should().dispose();
	}
}
