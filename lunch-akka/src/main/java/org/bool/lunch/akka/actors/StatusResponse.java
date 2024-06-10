package org.bool.lunch.akka.actors;

import org.bool.lunch.akka.Command;

public record StatusResponse(String name, String pid, Object info, Integer exitCode) implements Command {

}
