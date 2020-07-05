package org.bool.lunch.scalecube;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

import io.scalecube.services.ServiceReference;
import io.scalecube.services.api.ServiceMessage;

public class ServiceFilterFactory {
	
	private static final BiPredicate<ServiceReference, ServiceMessage> NO_FILTER = (s, m) -> true;
	
	private BiPredicate<ServiceReference, ServiceMessage> filter = NO_FILTER;

	public BiPredicate<ServiceReference, ServiceMessage> getFilter() {
		return filter;
	}
	
	public ServiceFilter create() {
		return filter::test;
	}
	
	public ServiceFilterFactory withQualifier() {
		return and((service, message) -> Objects.equals(service.qualifier(), message.qualifier()));
	}
	
	public ServiceFilterFactory withContentType() {
		return and((service, message) -> service.contentTypes().contains(message.dataFormatOrDefault()));
	}
	
	public ServiceFilterFactory withTag(String key) {
		return and((service, message) -> {
			String value = message.header(key);
			return value == null || value.equals(service.tags().get(key));  
		});
	}
	
	public ServiceFilterFactory withId(String key) {
		return and((service, message) -> {
			String value = message.header(key);
			return value == null || value.equals(service.endpointId());  
		});
	}
	
	private ServiceFilterFactory and(BiPredicate<ServiceReference, ServiceMessage> other) {
		this.filter = this.filter == NO_FILTER ? other : this.filter.and(other);
		return this;
	}
}
