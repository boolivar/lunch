package org.bool.lunch.akka.actors;

import org.bool.lunch.akka.Command;

public sealed interface LunchedItemCommand extends Command
	permits LunchedItemCommand.Terminate, LunchedItemCommand.Terminated, LunchedItemCommand.Status {

	record Terminate(boolean force) implements LunchedItemCommand {
	}

	record Terminated(Integer exitCode) implements LunchedItemCommand {
	}

	record Status() implements LunchedItemCommand {
	}
}
