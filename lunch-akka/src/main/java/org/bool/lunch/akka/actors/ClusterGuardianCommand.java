package org.bool.lunch.akka.actors;

import org.bool.lunch.LunchItem;
import org.bool.lunch.akka.Command;

import akka.actor.typed.ActorRef;

public sealed interface ClusterGuardianCommand extends Command
	permits ClusterGuardianCommand.Launch, ClusterGuardianCommand.Land, ClusterGuardianCommand.Status {

	public record Launch(LunchItem item) implements ClusterGuardianCommand {
	}

	public record Land(String name) implements ClusterGuardianCommand {
	}

	public record Status(ActorRef<StatusResponse> replyTo) implements ClusterGuardianCommand {
	}
}
