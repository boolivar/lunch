package org.bool.lunch.akka;

import org.bool.lunch.Lunch;
import org.bool.lunch.akka.actors.ClusterGuardianActor;
import org.bool.lunch.akka.actors.ClusterGuardianCommand;
import org.bool.lunch.config.YamlObjectFactory;

import akka.actor.Address;
import akka.actor.AddressFromURIString;
import akka.actor.typed.ActorSystem;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.Join;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class AkkaLunch {

	public Mono<ActorSystem<ClusterGuardianCommand>> run(Lunch lunch) {
		var address = StringUtils.firstNonEmpty(lunch.getAddress(), System.getenv("LUNCH_ADDRESS"), "localhost:5000");
		return address.startsWith("akka://")
			? run(lunch, Optional.of(AddressFromURIString.parse(address)))
			: run(lunch, Optional.empty(), StringUtils.split(address, ':'));
	}

	private Mono<ActorSystem<ClusterGuardianCommand>> run(Lunch lunch, Optional<Address> bootstrap, String... gatewayAddress) {
		var actorSystem = ActorSystem.create(ClusterGuardianActor.create(), "LunchGuardian");
		var cluster = Cluster.get(actorSystem);
		var selfAddress = cluster.selfMember().address();

		cluster.manager().tell(new Join(bootstrap.orElse(selfAddress)));
		if (lunch.getItems() != null) {
			lunch.getItems().forEach(item -> actorSystem.tell(new ClusterGuardianCommand.Launch(item)));
		}
		log.atInfo().setMessage("Actor {} system tree: {}").addArgument(selfAddress).addArgument(actorSystem::printTree).log();

		if (bootstrap.isEmpty()) {
			return new HttpGateway(actorSystem)
				.listen(gatewayAddress[0], Integer.parseInt(gatewayAddress[1]))
				.doOnNext(gateway -> actorSystem.getWhenTerminated().thenRun(gateway::dispose))
				.thenReturn(actorSystem);
		}
		return Mono.just(actorSystem);
	}

	public static void main(String[] args) {
		Flux.fromArray(args).next()
			.flatMap(arg -> new YamlObjectFactory<>(Lunch.class).readString(arg))
			.switchIfEmpty(Mono.fromSupplier(Lunch::new))
			.flatMap(lunch -> new AkkaLunch().run(lunch))
			.block();
	}
}