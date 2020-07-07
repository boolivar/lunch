package org.bool.lunch.akka;

import org.bool.lunch.LunchItem;

import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;

public class ActorCluster implements Cluster {

	private final ActorContext<?> context;
	
	public ActorCluster(ActorContext<?> context) {
		this.context = context;
	}
	
	@Override
	public void start(LunchItem lunch) {
		context.spawn(Behaviors.empty(), lunch.getName());
	}
}
