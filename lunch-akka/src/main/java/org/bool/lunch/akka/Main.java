package org.bool.lunch.akka;

import org.bool.lunch.akka.actors.ClusterGuardianActor;

import akka.actor.typed.ActorSystem;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import reactor.netty.DisposableServer;

@AllArgsConstructor
public class Main {

	public static void main(String[] args) {
		var address = address(args);
		if (StringUtils.startsWith(address, "akka://")) {
			run(new AkkaCluster(address), null, 0);
		} else {
			var gateway = StringUtils.split(address, ':');
			run(new AkkaCluster(), gateway[0], Integer.parseInt(gateway[1]));
		}
	}

	public static void run(AkkaCluster cluster, String gatewayHost, int gatewayPort) {
		var actorSystem = ActorSystem.create(ClusterGuardianActor.create(), "LunchGuardian");
		cluster.join(actorSystem);
		if (gatewayHost != null) {
			new HttpGateway(actorSystem).listen(gatewayHost, gatewayPort)
				.flatMap(DisposableServer::onDispose).block();
		}
	}

	private static String address(String... args) {
		var address = args.length > 0 ? args[0] : "5000";
		return StringUtils.contains(address, ':') ? address : "localhost:" + address;
	}
}