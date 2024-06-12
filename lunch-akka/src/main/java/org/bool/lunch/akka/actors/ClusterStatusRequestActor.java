package org.bool.lunch.akka.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.javadsl.TimerScheduler;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterStatusRequestActor extends AbstractBehavior<ClusterStatusRequestCommand> {

	private final ActorRef<ClusterStatusResponse> replyTo;

	private final Map<String, List<StatusResponse>> stats = new HashMap<>();

	private int size;

	public static Behavior<ClusterStatusRequestCommand> create(ActorRef<ClusterStatusResponse> replyTo) {
		return Behaviors.setup(context -> Behaviors.withTimers(timers -> create(context, timers, replyTo, LunchActor.SERVICE_KEY)));
	}

	public static Behavior<ClusterStatusRequestCommand> create(ActorContext<ClusterStatusRequestCommand> context,
			TimerScheduler<ClusterStatusRequestCommand> timers, ActorRef<ClusterStatusResponse> replyTo, ServiceKey<?> key) {
		timers.startSingleTimer(ClusterStatusRequestCommand.Timeout.EXPIRED, Duration.ofSeconds(30));
		var messageAdapter = context.messageAdapter(Receptionist.Listing.class, ClusterStatusRequestCommand.ClusterListing::new);
		context.getSystem().receptionist().tell(Receptionist.find(key, messageAdapter));
		return new ClusterStatusRequestActor(context, replyTo);
	}

	ClusterStatusRequestActor(ActorContext<ClusterStatusRequestCommand> context, ActorRef<ClusterStatusResponse> replyTo) {
		super(context);
		this.replyTo = replyTo;
	}

	@Override
	public Receive<ClusterStatusRequestCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(ClusterStatusRequestCommand.ClusterListing.class, this::listing)
			.onMessage(ClusterStatusRequestCommand.LunchStatus.class, this::status)
			.onMessageEquals(ClusterStatusRequestCommand.Timeout.EXPIRED, this::expired)
			.onSignal(Terminated.class, this::terminated)
			.build();
	}

	private Behavior<ClusterStatusRequestCommand> listing(ClusterStatusRequestCommand.ClusterListing listing) {
		var instances = listing.response().getServiceInstances(LunchActor.SERVICE_KEY);
		var request = new LunchCommand.Status(getContext().getSelf().narrow());
		instances.forEach(instance -> {
			instance.tell(request);
			getContext().watch(instance);
		});
		size = instances.size();
		return respond();
	}

	private Behavior<ClusterStatusRequestCommand> status(ClusterStatusRequestCommand.LunchStatus status) {
		stats.put(status.id(), status.stats());
		return respond();
	}

	private Behavior<ClusterStatusRequestCommand> terminated(Terminated terminated) { // NOPMD
		--size;
		return respond();
	}

	private Behavior<ClusterStatusRequestCommand> expired() {
		return reply();
	}

	private Behavior<ClusterStatusRequestCommand> respond() {
		return stats.size() >= size ? reply() : this;
	}

	private Behavior<ClusterStatusRequestCommand> reply() {
		replyTo.tell(new ClusterStatusResponse(stats));
		return Behaviors.stopped();
	}
}
