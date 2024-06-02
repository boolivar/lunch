package org.bool.lunch.akka;

import akka.actor.Address;
import akka.actor.AddressFromURIString;
import akka.actor.typed.ActorSystem;
import akka.cluster.typed.Cluster;
import akka.cluster.typed.Join;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class AkkaCluster {

	private final Optional<Address> bootstrapAddress;

	public AkkaCluster() {
		this(Optional.empty());
	}

	public AkkaCluster(String bootsrapAddress) {
		this(AddressFromURIString.parse(bootsrapAddress));
	}

	public AkkaCluster(Address bootstrapAddress) {
		this(Optional.of(bootstrapAddress));
	}

	public void join(ActorSystem<?> actorSystem) {
		var cluster = Cluster.get(actorSystem);
		var selfAddress = cluster.selfMember().address();
		cluster.manager().tell(new Join(bootstrapAddress.orElse(selfAddress)));
		log.atInfo().setMessage("Actor {} system tree: {}").addArgument(selfAddress).addArgument(actorSystem::printTree).log();
	}
}
