package org.bool.lunch;

import java.util.Collection;

public interface Runner {
	LunchProcess run(String command, Collection<String> args);
}
