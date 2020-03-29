package org.bool.lunch.akka;

import org.bool.lunch.Command;
import org.bool.lunch.LaunchPad;
import org.bool.lunch.Lunch;
import org.bool.lunch.akka.LunchActor.LunchCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.javadsl.Behaviors;

public class AkkaPad implements LaunchPad {

	private final Logger log = LoggerFactory.getLogger(AkkaPad.class);
	
	private final LunchItemActorFactory lunchItemActorFactory;

	public AkkaPad(LunchItemActorFactory lunchItemActorFactory) {
		this.lunchItemActorFactory = lunchItemActorFactory;
	}

	@Override
	public void launch(Lunch lunch) {
		ActorSystem<Command> actorSystem = ActorSystem
				.create(Behaviors.setup(context -> new LunchActor(context, lunchItemActorFactory)), "Lunch");
		lunch.getItems().stream().map(LunchCommand::new).forEach(actorSystem::tell);
		log.debug("Actor system tree: {}", actorSystem.printTree());
	}
}
