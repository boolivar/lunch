package org.bool.lunch.scalecube;

import io.scalecube.services.ServiceReference;
import io.scalecube.services.api.ServiceMessage;

@FunctionalInterface
public interface ServiceFilter {
	boolean accept(ServiceReference service, ServiceMessage message);
}
