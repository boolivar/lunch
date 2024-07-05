package org.bool.lunch.akka.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;

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
			.onMessage(ClusterGuardianCommand.Status.class, this::status)
			.build();
	}

	public Behavior<ClusterGuardianCommand> launch(ClusterGuardianCommand.Launch launch) {
		lunch.tell(new LunchCommand.Launch(launch.item()));
		return this;
	}

	public Behavior<ClusterGuardianCommand> land(ClusterGuardianCommand.Land land) {
		var landActor = getContext()
			.spawnAnonymous(Behaviors.receive(Receptionist.Listing.class).onAnyMessage(listing -> land(listing, land)).build());
		getContext().getSystem().receptionist().tell(Receptionist.find(LunchActor.SERVICE_KEY, landActor));
		return this;
	}

	public Behavior<ClusterGuardianCommand> status(ClusterGuardianCommand.Status status) {
		getContext().spawnAnonymous(ClusterStatusRequestActor.create(status.replyTo()));
		return this;
	}

	private Behavior<Receptionist.Listing> land(Receptionist.Listing listing, ClusterGuardianCommand.Land land) {
		var landLunch = new LunchCommand.Land(land.name());
		listing.getServiceInstances(LunchActor.SERVICE_KEY).forEach(lunch -> lunch.tell(landLunch));
		return Behaviors.stopped();
	}
}
