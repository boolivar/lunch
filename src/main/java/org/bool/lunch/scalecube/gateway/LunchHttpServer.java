package org.bool.lunch.scalecube.gateway;

import org.reactivestreams.Publisher;

import java.util.function.BiFunction;

import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class LunchHttpServer {

	private final String host;
	
	private final int port;

	private final BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> handler;

	public LunchHttpServer(String host, int port,
			BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> handler) {
		this.host = host;
		this.port = port;
		this.handler = handler;
	}
	
	public Mono<? extends DisposableServer> bind() {
		return Mono.defer(() -> HttpServer.create()
				.host(host)
				.port(port)
				.handle(handler)
				.bind());
	}
}
