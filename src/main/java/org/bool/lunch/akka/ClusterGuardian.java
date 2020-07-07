package org.bool.lunch.akka;

import org.bool.lunch.Command;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

public class ClusterGuardian {
	
	public interface ClusterCommand extends Command {
	}
	
	public static class StartProcess implements ClusterCommand {
	}

	private final String host;
	
	private final Integer port;
	
	private final Cluster cluster;

	public ClusterGuardian(String host, Integer port, Cluster cluster) {
		this.host = host;
		this.port = port;
		this.cluster = cluster;
	}
	
	public void runNode() {
	}
	
	public static ActorSystem<ClusterCommand> createActorSystem(String name, String host, Integer port) {
		return ActorSystem.create(Behaviors.setup(context -> create(host, port, context)), name);
	}
	
	private static Behavior<ClusterCommand> create(String host, Integer port, ActorContext<ClusterCommand> context) {
		ClusterGuardian guardian = new ClusterGuardian(host, port, new ActorCluster(context));
		return Behaviors.receive(ClusterCommand.class).onAnyMessage((ctx, message) -> dispatch(message, guardian)).build();
	}

	private static Behavior<ClusterCommand> dispatch(ClusterCommand message, ClusterGuardian guardian) {
		if (message instanceof StartProcess) {
			guardian.runNode();
		}
		return Behaviors.same();
	}
}
