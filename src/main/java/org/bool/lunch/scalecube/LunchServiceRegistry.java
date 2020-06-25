package org.bool.lunch.scalecube;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.scalecube.services.ServiceEndpoint;
import io.scalecube.services.ServiceReference;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.services.registry.api.ServiceRegistry;

public class LunchServiceRegistry implements ServiceRegistry {

	private final ServiceFilter serviceFilter;
	
	private final List<ServiceEndpoint> endpoints;
	
	public LunchServiceRegistry(ServiceFilter serviceFilter) {
		this(serviceFilter, new CopyOnWriteArrayList<>());
	}
	
	public LunchServiceRegistry(ServiceFilter serviceFilter, List<ServiceEndpoint> endpoints) {
		this.serviceFilter = serviceFilter;
		this.endpoints = endpoints;
	}
	
	@Override
	public List<ServiceEndpoint> listServiceEndpoints() {
		return endpoints;
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
		return endpoints.stream()
				.flatMap(e -> e.serviceReferences().stream())
				.filter(filter)
				.collect(Collectors.toList())
				;
	}
	
	@Override
	public boolean registerService(ServiceEndpoint serviceEndpoint) {
		return endpoints.add(serviceEndpoint);
	}

	@Override
	public ServiceEndpoint unregisterService(String endpointId) {
		endpoints.removeIf(e -> endpointId.equals(e.id()));
		return null;
	}
}
