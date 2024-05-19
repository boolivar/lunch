package org.bool.lunch.scalecube.gateway;

import io.scalecube.net.Address;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;

public class LunchHttpServer {

	private final Address address;

	private final HttpServer server;

	public LunchHttpServer(String host, int port, BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> handler) {
		this(Address.create(host, port), HttpServer.create().host(host).port(port).handle(handler));
	}

	public LunchHttpServer(Address address, HttpServer server) {
		this.address = address;
		this.server = server;
	}

	public Address getAddress() {
		return address;
	}

	public Mono<? extends DisposableServer> bind() {
		return server.bind();
	}
}
