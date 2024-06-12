package org.bool.lunch.akka.actors;

import org.bool.lunch.akka.Command;

import akka.actor.typed.receptionist.Receptionist;

import java.util.List;

public sealed interface ClusterStatusRequestCommand extends Command {

	record ClusterListing(Receptionist.Listing response) implements ClusterStatusRequestCommand {
	}

	record LunchStatus(String id, List<StatusResponse> stats) implements ClusterStatusRequestCommand {
	}

	enum Timeout implements ClusterStatusRequestCommand {
		EXPIRED;
	}
}
