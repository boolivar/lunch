package org.bool.lunch.scalecube;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import io.scalecube.services.ServiceEndpoint;
import io.scalecube.services.ServiceReference;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.services.registry.api.ServiceRegistry;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LunchServiceRegistry implements ServiceRegistry {

	private final ServiceFilter serviceFilter;

	private final Map<String, ServiceEndpoint> endpoints;

	public LunchServiceRegistry(ServiceFilter serviceFilter) {
		this(serviceFilter, new ConcurrentHashMap<>());
	}

	@Override
	public List<ServiceEndpoint> listServiceEndpoints() {
		return List.copyOf(endpoints.values());
	}

	@Override
	public List<ServiceReference> listServiceReferences() {
		return findServiceReferences(ref -> true);
	}

	@Override
	public List<ServiceReference> lookupService(ServiceMessage request) {
		return findServiceReferences(ref -> serviceFilter.accept(ref, request));
	}

	private List<ServiceReference> findServiceReferences(Predicate<ServiceReference> filter) {
		return endpoints.values().stream()
				.flatMap(e -> e.serviceReferences().stream())
				.filter(filter)
				.toList();
	}
	
	@Override
	public boolean registerService(ServiceEndpoint serviceEndpoint) {
		return endpoints.putIfAbsent(serviceEndpoint.id(),  serviceEndpoint) == null;
	}

	@Override
	public ServiceEndpoint unregisterService(String endpointId) {
		return endpoints.remove(endpointId);
	}
}
