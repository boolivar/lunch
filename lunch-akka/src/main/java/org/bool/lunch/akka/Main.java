package org.bool.lunch.akka;

import org.bool.lunch.akka.actors.LunchActor;
import org.bool.lunch.akka.actors.LunchCommand;

import akka.actor.typed.ActorSystem;
import org.apache.commons.lang3.StringUtils;
import reactor.netty.DisposableServer;

public class Main {

	public static void main(String[] args) {
		var address = args.length > 0 ? StringUtils.split(args[0], ':') : new String[] { "0" };
		var hostport = address.length > 1 ? address : new String[] { "localhost", address[0] };
		ActorSystem<LunchCommand> actorSystem = ActorSystem.create(LunchActor.create(), "Lunch");
		new AkkaCluster().join(actorSystem);
		new HttpGateway(actorSystem).listen(hostport[0], Integer.parseInt(hostport[1]))
			.flatMap(DisposableServer::onDispose).block();
	}
}