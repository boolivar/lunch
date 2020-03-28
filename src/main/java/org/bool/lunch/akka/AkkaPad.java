package org.bool.lunch.akka;

import org.bool.lunch.Command;
import org.bool.lunch.Lunch;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;

public class AkkaPad {
	
	private final LunchItemActorFactory lunchItemActorFactory;

	public AkkaPad(LunchItemActorFactory lunchItemActorFactory) {
		this.lunchItemActorFactory = lunchItemActorFactory;
	}

	public void launch(Lunch lunch) {
		ActorSystem<Command> actorSystem = ActorSystem
				.create(Behaviors.setup(context -> new LunchActor(context, lunchItemActorFactory)), "Lunch");
	}
}
