package org.bool.lunch.scalecube.gateway;

import io.scalecube.net.Address;
import io.scalecube.services.gateway.Gateway;
import io.scalecube.services.gateway.GatewayOptions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class HttpGateway implements Gateway {

	private final HttpGatewayOptions options;

	private final HttpGatewayServerFactory serverFactory;

	private final DisposableServer server;

	public HttpGateway(GatewayOptions options, String host, HttpGatewayServerFactory serverFactory) {
		this(HttpGatewayOptions.builder().id(options.id()).host(host).port(options.port()).serviceCall(options.call()).build(), serverFactory);
	}

	public HttpGateway(HttpGatewayOptions options, HttpGatewayServerFactory serverFactory) {
		this(options, serverFactory, null);
	}

	@Override
	public String id() {
		return options.getId();
	}

	@Override
	public Address address() {
		return server != null
			? Address.create(server.host(), server.port())
			: Address.create(options.getHost(), options.getPort());
	}

	@Override
	public Mono<Gateway> start() {
		return serverFactory.create(options.getHost(), options.getPort(), options.getServiceCall())
			.map(startedServer -> new HttpGateway(options, serverFactory, startedServer));
	}

	@Override
	public Mono<Void> stop() {
		return Mono.justOrEmpty(server)
			.doOnNext(DisposableServer::dispose).flatMap(DisposableServer::onDispose);
	}
}
