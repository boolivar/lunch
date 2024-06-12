package org.bool.lunch.akka.actors;

import org.bool.lunch.LunchItem;
import org.bool.lunch.akka.Command;

import akka.actor.typed.ActorRef;

public sealed interface ClusterGuardianCommand extends Command
	permits ClusterGuardianCommand.Launch, ClusterGuardianCommand.Land, ClusterGuardianCommand.Status {

	record Launch(LunchItem item) implements ClusterGuardianCommand {
	}

	record Land(String name) implements ClusterGuardianCommand {
	}

	record Status(ActorRef<ClusterStatusResponse> replyTo) implements ClusterGuardianCommand {
	}
}
