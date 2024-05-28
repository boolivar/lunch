package org.bool.lunch.scalecube.gateway;

import io.scalecube.services.ServiceCall;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;

@FunctionalInterface
public interface HttpGatewayServerFactory {
	Mono<? extends DisposableServer> create(String host, int port, ServiceCall serviceCall);
}
