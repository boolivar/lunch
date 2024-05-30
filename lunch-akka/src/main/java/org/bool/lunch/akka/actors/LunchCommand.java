package org.bool.lunch.akka.actors;

import org.bool.lunch.LunchItem;
import org.bool.lunch.akka.Command;
import org.bool.lunch.api.LunchedItem;

public sealed interface LunchCommand extends Command
	permits LunchCommand.Launch, LunchCommand.Lunched, LunchCommand.Land, LunchCommand.Status {

	record Launch(LunchItem item) implements LunchCommand {
	}

	record Lunched(LunchedItem item) implements LunchCommand {
	}

	record Land(String name) implements LunchCommand {
	}

	record Status() implements LunchCommand {
	}
}
