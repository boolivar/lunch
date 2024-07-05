package org.bool.lunch.akka;

import org.bool.lunch.Lunch;
import org.bool.lunch.akka.actors.ClusterGuardianActor;
import org.bool.lunch.akka.actors.ClusterGuardianCommand;

import akka.actor.Address;
import akka.actor.AddressFromURIString;
import akka.actor.typed.ActorSystem;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.Join;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class AkkaLunch {

	public static final String LUNCH_ADDRESS_ENV = "LUNCH_ADDRESS";

	private final String address;

	private final Lunch lunch;

	public AkkaLunch(Lunch lunch) {
		this(StringUtils.defaultIfEmpty(System.getenv(LUNCH_ADDRESS_ENV), "localhost:5000"), lunch);
	}

	public Mono<ActorSystem<ClusterGuardianCommand>> run() {
		return address.startsWith("akka://")
			? run(Optional.of(AddressFromURIString.parse(address)))
			: run(Optional.empty(), StringUtils.split(address, ':'));
	}

	private Mono<ActorSystem<ClusterGuardianCommand>> run(Optional<Address> bootstrap, String... gatewayAddress) {
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
}