package org.bool.lunch.akka.actors;

import org.bool.lunch.akka.Command;

public record StatusResponse(String name, String pid, String info) implements Command {

}
