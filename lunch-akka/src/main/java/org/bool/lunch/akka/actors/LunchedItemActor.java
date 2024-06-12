package org.bool.lunch.akka.actors;

import org.bool.lunch.api.LunchedItem;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.Objects;

public class LunchedItemActor extends AbstractBehavior<LunchedItemCommand> {

	private final LunchedItem item;

	public static Behavior<LunchedItemCommand> create(LunchedItem item) {
		return Behaviors.setup(context -> create(context, item));
	}

	public static LunchedItemActor create(ActorContext<LunchedItemCommand> context, LunchedItem item) {
		item.exitCode().subscribe(exitCode -> context.getSelf().tell(new LunchedItemCommand.Terminated(exitCode)));
		return new LunchedItemActor(context, item);
	}

	LunchedItemActor(ActorContext<LunchedItemCommand> context, LunchedItem item) {
		super(context);
		this.item = item;
	}

	@Override
	public Receive<LunchedItemCommand> createReceive() {
		return newReceiveBuilder()
			.onMessage(LunchedItemCommand.Terminate.class, this::terminate)
			.onMessage(LunchedItemCommand.Terminated.class, this::terminated)
			.onMessage(LunchedItemCommand.Status.class, this::status)
			.onSignal(PostStop.class, this::postStop)
			.build();
	}

	private Behavior<LunchedItemCommand> terminate(LunchedItemCommand.Terminate terminate) {
		getContext().getLog().info("{} item {}", terminate, item);
		item.terminate(terminate.force()).subscribe();
		return this;
	}

	private Behavior<LunchedItemCommand> terminated(LunchedItemCommand.Terminated terminated) {
		getContext().getLog().info("Item {}: {}", item, terminated);
		return Behaviors.stopped();
	}

	private Behavior<LunchedItemCommand> status(LunchedItemCommand.Status status) {
		var response = new StatusResponse(item.getName(), item.getPid(), Objects.toString(item.getInfo(), null));
		status.replyTo().tell(new LunchStatusRequestCommand.Status(response));
		return this;
	}

	private Behavior<LunchedItemCommand> postStop(PostStop signal) {
		if (item.isAlive()) {
			getContext().getLog().info("Terminating item {} on {} signal", item, signal);
			item.terminate(true).subscribe();
		}
		return this;
	}
}
