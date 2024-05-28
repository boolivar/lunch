package org.bool.lunch.scalecube.gateway;

import io.scalecube.services.ServiceCall;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@RequiredArgsConstructor
public class NettyHttpGatewayServerFactory implements HttpGatewayServerFactory {

	private final HttpGatewayHandlerFactory handlerFactory;

	@Override
	public Mono<? extends DisposableServer> create(String host, int port, ServiceCall serviceCall) {
		return Mono.defer(() -> HttpServer.create()
			.host(host)
			.port(port)
			.handle(handlerFactory.create(serviceCall))
			.bind());
	}
}
