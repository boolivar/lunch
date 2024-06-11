package org.bool.lunch.akka;

import org.bool.lunch.LunchItem;
import org.bool.lunch.akka.actors.ClusterGuardianCommand;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.AskPattern;
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
import java.time.Duration;

@RequiredArgsConstructor
public class HttpGateway {

	private final ActorSystem<? super ClusterGuardianCommand> lunchSystem;

	private final ObjectMapper jsonMapper;

	public HttpGateway(ActorSystem<? super ClusterGuardianCommand> lunchSystem) {
		this(lunchSystem, new ObjectMapper().findAndRegisterModules());
	}

	public Mono<? extends DisposableServer> listen(String host, int port) {
		return HttpServer.create().host(host).port(port)
			.route(routes -> routes
				.post("/launch", this::launch)
				.post("/land/{name}", this::land)
				.get("/stats", this::stats)
				.get("/tree", this::tree)
				.get("/cluster", this::cluster))
			.bind();
	}

	private Mono<Void> launch(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.map(this::parseItem)
			.doOnNext(item -> lunchSystem.tell(new ClusterGuardianCommand.Launch(item)))
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
			.doOnNext(name -> lunchSystem.tell(new ClusterGuardianCommand.Land(name)))
			.then(response.send());
	}

	private Mono<Void> stats(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.then(response.sendString(status()).then());
	}

	private Mono<String> status() {
		return Mono.fromCompletionStage(AskPattern.ask(lunchSystem, ClusterGuardianCommand.Status::new, Duration.ofSeconds(10), lunchSystem.scheduler()))
			.map(this::toJson);
	}

	@SneakyThrows
	private String toJson(Object value) {
		return jsonMapper.writeValueAsString(value);
	}

	private Mono<Void> tree(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.then(response.sendString(Mono.fromSupplier(lunchSystem::printTree)).then());
	}

	private Mono<Void> cluster(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.then(response.sendString(Mono.just(lunchSystem).map(Cluster::get).map(Cluster::state).map(String::valueOf)).then());
	}
}
