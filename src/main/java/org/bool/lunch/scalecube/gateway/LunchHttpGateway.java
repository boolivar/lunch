package org.bool.lunch.scalecube.gateway;

import io.scalecube.net.Address;
import io.scalecube.services.gateway.Gateway;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;

public class LunchHttpGateway implements Gateway {

	private final String id;
	
	private final Address address;
	
	private final Mono<? extends DisposableServer> server;
	
	public LunchHttpGateway(String id, String host, int port, Mono<? extends DisposableServer> server) {
	    this(id, Address.create(host, port), server);
    }
	
	public LunchHttpGateway(String id, Address address, Mono<? extends DisposableServer> server) {
		this.id = id;
		this.address = address;
		this.server = server;
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public Address address() {
		return address;
	}

	@Override
	public Mono<Gateway> start() {
		return server.thenReturn(this);
	}

	@Override
	public Mono<Void> stop() {
		return server.doOnNext(DisposableServer::dispose).then();
	}
}
