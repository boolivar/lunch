package org.bool.lunch;

import java.util.Collection;

public interface Runner {
	Process run(String command, Collection<String> args);
}
