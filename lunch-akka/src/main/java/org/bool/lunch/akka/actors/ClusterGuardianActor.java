package org.bool.lunch.akka.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class ClusterGuardianActor extends AbstractBehavior<ClusterGuardianCommand> {

	private final ActorRef<LunchCommand> lunch;

	public static Behavior<ClusterGuardianCommand> create() {
		return Behaviors.setup(ClusterGuardianActor::create);
	}

	public static Behavior<ClusterGuardianCommand> create(ActorContext<ClusterGuardianCommand> context) {
		return new ClusterGuardianActor(context, context.spawn(LunchActor.create(), "lunch"));
	}

	ClusterGuardianActor(ActorContext<ClusterGuardianCommand> context, ActorRef<LunchCommand> lunch) {
		super(context);
		this.lunch = lunch;
	}

	@Override
	public Receive<ClusterGuardianCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(ClusterGuardianCommand.Launch.class, this::launch)
			.onMessage(ClusterGuardianCommand.Land.class, this::land)
			.build();
	}

	public Behavior<ClusterGuardianCommand> launch(ClusterGuardianCommand.Launch launch) {
		lunch.tell(new LunchCommand.Launch(launch.item()));
		return this;
	}

	public Behavior<ClusterGuardianCommand> land(ClusterGuardianCommand.Land land) {
		lunch.tell(new LunchCommand.Land(land.name()));
		return this;
	}
}
