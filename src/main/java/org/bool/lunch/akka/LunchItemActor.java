package org.bool.lunch.akka;

import org.bool.lunch.Command;
import org.bool.lunch.LunchItem;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;

public class LunchItemActor extends AbstractBehavior<Command> {

	private final LunchItem item;
	
	public LunchItemActor(ActorContext<Command> context, LunchItem item) {
		super(context);
		this.item = item;
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
				.onAnyMessage(this::handle)
				.build();
	}
	
	private Behavior<Command> handle(Command cmd) {
		return this;
	}
}
