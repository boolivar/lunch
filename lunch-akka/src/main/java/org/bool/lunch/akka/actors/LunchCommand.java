package org.bool.lunch.akka.actors;

import org.bool.lunch.LunchItem;
import org.bool.lunch.akka.Command;
import org.bool.lunch.api.LunchedItem;

import akka.actor.typed.ActorRef;

public sealed interface LunchCommand extends Command
	permits LunchCommand.Launch, LunchCommand.Lunched, LunchCommand.Land, LunchCommand.Terminated, LunchCommand.Status {

	record Launch(LunchItem item) implements LunchCommand {
	}

	record Lunched(LunchedItem item) implements LunchCommand {
	}

	record Land(String name) implements LunchCommand {
	}

	record Terminated(LunchedItem item) implements LunchCommand {
	}

	record Status(ActorRef<StatusResponse> replyTo) implements LunchCommand {
	}
}
