package org.bool.lunch.akka;

import org.bool.lunch.Command;
import org.bool.lunch.LunchBox;
import org.bool.lunch.LunchItem;
import org.bool.lunch.Lunched;

import akka.actor.typed.javadsl.ActorContext;

public class LunchItemActorFactory {

	private final LunchBox lunchBox;
	
	public LunchItemActorFactory(LunchBox lunchBox) {
		this.lunchBox = lunchBox;
	}

	public LunchItemActor create(ActorContext<Command> context, LunchItem item) {
		Lunched lunched = lunchBox.launch(item, (i, ec) -> context.getSelf().tell(new LunchItemActor.Stopped(ec)));
		return new LunchItemActor(context, lunched);
	}
}
