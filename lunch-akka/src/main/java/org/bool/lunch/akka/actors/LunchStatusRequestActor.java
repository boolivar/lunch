package org.bool.lunch.akka.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LunchStatusRequestActor extends AbstractBehavior<LunchStatusRequestCommand> {

	private final ActorRef<ClusterStatusRequestCommand.LunchStatus> replyTo;

	private final List<StatusResponse> stats;

	private int size;

	public static Behavior<LunchStatusRequestCommand> create(ActorRef<ClusterStatusRequestCommand.LunchStatus> replyTo, Collection<ActorRef<LunchedItemCommand>> lunched) {
		return Behaviors.setup(context -> create(context, replyTo, lunched));
	}

	public static Behavior<LunchStatusRequestCommand> create(ActorContext<LunchStatusRequestCommand> context, ActorRef<ClusterStatusRequestCommand.LunchStatus> replyTo, Collection<ActorRef<LunchedItemCommand>> lunched) {
		if (!lunched.isEmpty()) {
			var request = new LunchedItemCommand.Status(context.getSelf().narrow());
			lunched.forEach(actor -> actor.tell(request));
			lunched.forEach(context::watch);
			return new LunchStatusRequestActor(context, replyTo, new ArrayList<>(lunched.size()), lunched.size());
		}
		return respond(context, replyTo, List.of());
	}

	LunchStatusRequestActor(ActorContext<LunchStatusRequestCommand> context, ActorRef<ClusterStatusRequestCommand.LunchStatus> replyTo,
			List<StatusResponse> stats, int size) {
		super(context);
		this.replyTo = replyTo;
		this.stats = stats;
		this.size = size;
	}

	@Override
	public Receive<LunchStatusRequestCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(LunchStatusRequestCommand.Status.class, this::status)
			.onSignal(Terminated.class, this::terminated)
			.build();
	}

	private Behavior<LunchStatusRequestCommand> status(LunchStatusRequestCommand.Status status) {
		stats.add(status.response());
		return respond();
	}

	private Behavior<LunchStatusRequestCommand> terminated(Terminated terminated) { // NOPMD
		--size;
		return respond();
	}

	private Behavior<LunchStatusRequestCommand> respond() {
		return stats.size() >= size ? respond(getContext(), replyTo, stats) : this;
	}

	private static Behavior<LunchStatusRequestCommand> respond(ActorContext<LunchStatusRequestCommand> context,
			ActorRef<ClusterStatusRequestCommand.LunchStatus> replyTo, List<StatusResponse> response) {
		replyTo.tell(new ClusterStatusRequestCommand.LunchStatus(context.getSelf().path().parent().toStringWithAddress(context.getSystem().address()), response));
		return Behaviors.stopped();
	}
}
