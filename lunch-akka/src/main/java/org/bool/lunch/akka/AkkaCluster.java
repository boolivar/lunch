package org.bool.lunch.akka;

import akka.actor.Address;
import akka.actor.AddressFromURIString;
import akka.actor.typed.ActorSystem;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.Join;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class AkkaCluster {

	private final Optional<Address> bootstrap;

	public AkkaCluster() {
		this(Optional.empty());
	}

	public AkkaCluster(String address) {
		this(AddressFromURIString.parse(address));
	}

	public AkkaCluster(Address address) {
		this(Optional.of(address));
	}

	public void join(ActorSystem<?> actorSystem) {
		var cluster = Cluster.get(actorSystem);
		var address = cluster.selfMember().address();
		cluster.manager().tell(new Join(bootstrap.orElse(address)));
		log.atInfo().setMessage("Actor {} system tree: {}").addArgument(address).addArgument(actorSystem::printTree).log();
	}
}
