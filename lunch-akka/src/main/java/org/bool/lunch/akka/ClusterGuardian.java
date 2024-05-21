package org.bool.lunch.akka;

import org.bool.lunch.LunchItem;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

public class ClusterGuardian {
	
	public interface ClusterCommand extends Command {
	}
	
	public static class StartProcess implements ClusterCommand {

		private final LunchItem lunchItem;

		public StartProcess(LunchItem lunchItem) {
			this.lunchItem = lunchItem;
		}

		public LunchItem getLunchItem() {
			return lunchItem;
		}
	}

	private final Cluster cluster;

	public ClusterGuardian(Cluster cluster) {
		this.cluster = cluster;
	}

	public void runNode(LunchItem lunchItem) {
		cluster.start(lunchItem);
	}

	public static ActorSystem<ClusterCommand> createActorSystem(String name, String host, Integer port) {
		return ActorSystem.create(Behaviors.setup(ClusterGuardian::create), name);
	}

	private static Behavior<ClusterCommand> create(ActorContext<ClusterCommand> context) {
		ClusterGuardian guardian = new ClusterGuardian(new ActorCluster(context));
		return Behaviors.receive(ClusterCommand.class)
				.onAnyMessage(message -> dispatch(message, guardian)).build();
	}

	private static Behavior<ClusterCommand> dispatch(ClusterCommand message, ClusterGuardian guardian) {
		if (message instanceof StartProcess) {
			guardian.runNode(((StartProcess) message).getLunchItem());
		}
		return Behaviors.same();
	}
}
