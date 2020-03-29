package org.bool.lunch.akka;

import org.bool.lunch.Command;
import org.bool.lunch.LunchItem;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class LunchActor extends AbstractBehavior<Command> {
	
	public static class LunchCommand implements Command {
		
		private final LunchItem item;

		public LunchCommand(LunchItem item) {
			this.item = item;
		}
		
		public LunchItem getItem() {
			return item;
		}
	}
	
	private final ActorContext<Command> context;
	
	private final LunchItemActorFactory actorFactory;
	
	public LunchActor(ActorContext<Command> context, LunchItemActorFactory actorFactory) {
		this.context = context;
		this.actorFactory = actorFactory;
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
				.onMessage(LunchCommand.class, this::lunch)
				.build();
	}
	
	private Behavior<Command> lunch(LunchCommand cmd) {
		ActorRef<Command> lunchItemActor = context
				.spawn(Behaviors.setup(ctx -> actorFactory.create(ctx, cmd.getItem())), cmd.getItem().getName());
		context.watch(lunchItemActor);
		return this;
	}
}
