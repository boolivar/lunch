package org.bool.lunch.scalecube.gateway;

import io.scalecube.net.Address;
import io.scalecube.services.gateway.Gateway;
import io.scalecube.services.gateway.GatewayOptions;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;

public class LunchHttpGateway implements Gateway {

	private final GatewayOptions options;

	private final LunchHttpServer server;

	private final DisposableServer instance;

	public LunchHttpGateway(GatewayOptions options, LunchHttpServer server) {
		this(options, server, null);
	}

	LunchHttpGateway(GatewayOptions options, LunchHttpServer server, DisposableServer instance) {
		this.options = options;
		this.server = server;
		this.instance = instance;
	}

	@Override
	public String id() {
		return options.id();
	}

	@Override
	public Address address() {
		return server.getAddress();
	}

	@Override
	public Mono<Gateway> start() {
		return server.bind().map(disposable -> new LunchHttpGateway(options, server, disposable));
	}

	@Override
	public Mono<Void> stop() {
		return Mono.justOrEmpty(instance).doOnNext(DisposableServer::dispose).then();
	}
}
