package org.bool.lunch.akka;

import org.bool.lunch.LunchItem;
import org.bool.lunch.akka.actors.LunchCommand;

import akka.actor.typed.ActorSystem;
import akka.cluster.typed.Cluster;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class HttpGateway {

	private final ActorSystem<? super LunchCommand> lunchSystem;

	private final ObjectMapper jsonMapper;

	public HttpGateway(ActorSystem<? super LunchCommand> lunchSystem) {
		this(lunchSystem, new ObjectMapper().findAndRegisterModules());
	}

	public Mono<? extends DisposableServer> listen(String host, int port) {
		return HttpServer.create().host(host).port(port)
			.route(routes -> routes
				.post("/launch", this::launch)
				.post("/land/{name}", this::land)
				.get("/stats", this::stats)
				.get("/cluster", this::cluster))
			.bind();
	}

	private Mono<Void> launch(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.map(this::parseItem)
			.doOnNext(item -> lunchSystem.tell(new LunchCommand.Launch(item)))
			.then(response.send());
	}

	@SneakyThrows
	private LunchItem parseItem(ByteBuf buf) {
		try (var in = new InputStreamReader(new ByteBufInputStream(buf), StandardCharsets.UTF_8)) {
			return jsonMapper.readValue(in, LunchItem.class);
		}
	}

	private Mono<Void> land(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.then(Mono.just(request.param("name")))
			.doOnNext(name -> lunchSystem.tell(new LunchCommand.Land(name)))
			.then(response.send());
	}

	private Mono<Void> stats(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.then(response.sendString(Mono.fromSupplier(lunchSystem::printTree)).then());
	}

	private Mono<Void> cluster(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.then(response.sendString(Mono.just(lunchSystem).map(Cluster::get).map(Cluster::state).map(String::valueOf)).then());
	}
}
