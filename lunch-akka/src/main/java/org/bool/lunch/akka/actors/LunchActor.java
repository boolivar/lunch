package org.bool.lunch.akka.actors;

import org.bool.lunch.api.LunchedItem;
import org.bool.lunch.api.Luncher;
import org.bool.lunch.core.LocalProcessLuncher;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LunchActor extends AbstractBehavior<LunchCommand> {

	public static final ServiceKey<LunchCommand> SERVICE_KEY = ServiceKey.create(LunchCommand.class, "LUNCH");

	private final Luncher luncher;

	private final Map<String, ActorRef<LunchedItemCommand>> lunchedItems = new HashMap<>();

	private final Map<String, List<LunchedItem>> terminatedItems;

	public static Behavior<LunchCommand> create() {
		return create(new LocalProcessLuncher());
	}

	public static Behavior<LunchCommand> create(Luncher luncher) {
		return Behaviors.setup(context -> create(context, luncher));
	}

	public static Behavior<LunchCommand> create(ActorContext<LunchCommand> context, Luncher luncher) {
		context.getSystem().receptionist().tell(Receptionist.register(SERVICE_KEY, context.getSelf()));
		return new LunchActor(context, luncher, new HashMap<>());
	}

	LunchActor(ActorContext<LunchCommand> context, Luncher luncher, Map<String, List<LunchedItem>> terminatedItems) {
		super(context);
		this.luncher = luncher;
		this.terminatedItems = terminatedItems;
	}

	@Override
	public Receive<LunchCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(LunchCommand.Launch.class, this::launch)
			.onMessage(LunchCommand.Lunched.class, this::lunched)
			.onMessage(LunchCommand.Land.class, this::land)
			.onMessage(LunchCommand.Terminated.class, this::terminated)
			.onMessage(LunchCommand.Status.class, this::status)
			.build();
	}

	private Behavior<LunchCommand> launch(LunchCommand.Launch launch) {
		getContext().getLog().info("Launch: {}", launch);
		luncher.launch(launch.item()).subscribe(item -> getContext().getSelf().tell(new LunchCommand.Lunched(item)));
		return this;
	}

	private Behavior<LunchCommand> lunched(LunchCommand.Lunched lunched) {
		getContext().getLog().info("Lunched: {}", lunched);
		var actor = getContext().spawn(LunchedItemActor.create(lunched.item()), lunched.item().getName());
		getContext().watchWith(actor, new LunchCommand.Terminated(lunched.item()));
		lunchedItems.put(lunched.item().getName(), actor);
		return this;
	}

	private Behavior<LunchCommand> land(LunchCommand.Land land) {
		var terminate = new LunchedItemCommand.Terminate(false);
		(land.name() != null ? Stream.ofNullable(lunchedItems.get(land.name())) : lunchedItems.values().stream())
			.forEach(item -> item.tell(terminate));
		return this;
	}

	private Behavior<LunchCommand> terminated(LunchCommand.Terminated terminated) {
		getContext().getLog().info("Terminated: {}", terminated);
		lunchedItems.remove(terminated.item().getName());
		terminatedItems.computeIfAbsent(terminated.item().getName(), k -> new ArrayList<>()).add(terminated.item());
		return this;
	}

	private Behavior<LunchCommand> status(LunchCommand.Status status) {
		getContext().spawnAnonymous(LunchStatusRequestActor.create(status.replyTo(), lunchedItems.values()));
		return this;
	}
}
