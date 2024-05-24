package org.bool.lunch.akka;

import org.bool.lunch.LunchItem;
import org.bool.lunch.api.Luncher;

import akka.actor.typed.javadsl.ActorContext;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LunchItemActorFactory {

	private final Luncher luncher;
	
	public LunchItemActor create(ActorContext<Command> context, LunchItem item) {
		var lunched = luncher.launch(item)
			.doOnNext(process -> process.exitCode().subscribe(ec -> context.getSelf().tell(new LunchItemActor.Stopped(ec))))
			.block();
		return new LunchItemActor(context, lunched);
	}
}
