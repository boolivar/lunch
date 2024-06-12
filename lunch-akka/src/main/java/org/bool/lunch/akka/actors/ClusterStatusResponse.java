package org.bool.lunch.akka.actors;

import org.bool.lunch.akka.Command;

import java.util.List;
import java.util.Map;

public record ClusterStatusResponse(Map<String, List<StatusResponse>> stats) implements Command {

}
