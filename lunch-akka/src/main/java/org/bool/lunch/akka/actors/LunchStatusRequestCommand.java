package org.bool.lunch.akka.actors;

public sealed interface LunchStatusRequestCommand {

	record Status(StatusResponse response) implements LunchStatusRequestCommand {
	}
}
