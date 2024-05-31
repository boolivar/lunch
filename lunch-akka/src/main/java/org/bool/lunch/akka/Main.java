package org.bool.lunch.akka;

import org.bool.lunch.LunchItem;
import org.bool.lunch.akka.actors.LunchActor;
import org.bool.lunch.akka.actors.LunchCommand;

import akka.actor.typed.ActorSystem;
import akka.cluster.typed.Cluster;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {

	private final ObjectMapper jsonMapper = new ObjectMapper().findAndRegisterModules();

	private final ActorSystem<LunchCommand> lunchSystem;

	public Main(ActorSystem<LunchCommand> lunchSystem) {
		this.lunchSystem = lunchSystem;
	}

	public void run(String host, int port) {
		new AkkaCluster().join(lunchSystem);
		HttpServer.create().host(host).port(port)
			.route(routes -> routes
				.post("/launch", this::launch)
				.post("/land/{name}", this::land)
				.get("/stats", this::stats)
				.get("/cluster", this::cluster))
			.bindNow()
			.onDispose().block();
	}

	private Mono<Void> launch(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.map(this::parseItem)
			.doOnNext(item -> lunchSystem.tell(new LunchCommand.Launch(item)))
			.then(response.sendString(Mono.just("Success")).then());
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
			.then(response.sendString(Mono.just("Success")).then());
	}

	private Mono<Void> stats(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.then(response.sendString(Mono.fromSupplier(lunchSystem::printTree)).then());
	}

	private Mono<Void> cluster(HttpServerRequest request, HttpServerResponse response) {
		return request.receive().aggregate()
			.then(response.sendString(Mono.just(lunchSystem).map(Cluster::get).map(Cluster::state).map(String::valueOf)).then());
	}

	public static void main(String[] args) {
		var address = args.length > 0 ? StringUtils.split(args[0], ':') : new String[] { "0" };
		var hostport = address.length > 1 ? address : new String[] { "localhost", address[0] };
		new Main(ActorSystem.create(LunchActor.create(), "Lunch")).run(hostport[0], Integer.parseInt(hostport[1]));
	}
}