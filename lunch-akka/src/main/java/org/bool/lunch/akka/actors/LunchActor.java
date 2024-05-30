package org.bool.lunch.akka.actors;

import org.bool.lunch.api.Luncher;
import org.bool.lunch.core.LocalProcessLuncher;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class LunchActor extends AbstractBehavior<LunchCommand> {

	private final Luncher luncher;

	public static Behavior<LunchCommand> create() {
		return create(new LocalProcessLuncher());
	}

	public static Behavior<LunchCommand> create(Luncher luncher) {
		return Behaviors.setup(context -> new LunchActor(context, luncher));
	}

	LunchActor(ActorContext<LunchCommand> context, Luncher luncher) {
		super(context);
		this.luncher = luncher;
	}

	@Override
	public Receive<LunchCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(LunchCommand.Launch.class, this::launch)
			.onMessage(LunchCommand.Lunched.class, this::lunched)
			.onMessage(LunchCommand.Land.class, this::land)
			.build();
	}

	private Behavior<LunchCommand> launch(LunchCommand.Launch launch) {
		getContext().getLog().info("Launch: {}", launch);
		luncher.launch(launch.item()).subscribe(item -> getContext().getSelf().tell(new LunchCommand.Lunched(item)));
		return this;
	}

	private Behavior<LunchCommand> lunched(LunchCommand.Lunched lunched) {
		getContext().getLog().info("Lunched: {}", lunched);
		getContext().spawn(LunchedItemActor.create(lunched.item()), lunched.item().getName());
		return this;
	}

	private Behavior<LunchCommand> land(LunchCommand.Land land) {
		getContext().getChild(land.name()).ifPresent(ref -> ref.unsafeUpcast().tell(new LunchedItemCommand.Terminate(false)));
		return this;
	}
}
