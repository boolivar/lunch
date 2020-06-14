package org.bool.lunch.scalecube;

import org.bool.lunch.CachedRunnerFactory;
import org.bool.lunch.DefaultRunnerFactory;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.PidReader;
import org.bool.lunch.RunnerType;

import java.util.concurrent.Executors;

import io.scalecube.services.Microservices;
import io.scalecube.services.ServiceEndpoint;
import io.scalecube.services.ServiceInfo;
import io.scalecube.services.discovery.ScalecubeServiceDiscovery;
import io.scalecube.services.discovery.api.ServiceDiscovery;

public class Lunch {

	private final LunchService service;

	public Lunch(LunchService service) {
		this.service = service;
	}

	public void launch() throws InterruptedException {
		Microservices microservices = Microservices.builder()
				.services(ServiceInfo.fromServiceInstance(service)
						.tag("role", "seed")
						.build())
				.discovery(this::discovery)
				.startAwait();
		
		synchronized (microservices) {
			microservices.wait();
		}
	}
		
	private ServiceDiscovery discovery(ServiceEndpoint endpoint) {
		return new ScalecubeServiceDiscovery(endpoint);
	}
	
	public static void main(String[] args) throws InterruptedException {
		CachedRunnerFactory<String> factory = new CachedRunnerFactory<>(new DefaultRunnerFactory(), RunnerType::valueOf);
		LunchRunner runner = new LunchRunner(factory::lookup, PidReader.DEFAULT);
		LocalLunchService lunchService = new LocalLunchService(runner, Executors.newSingleThreadExecutor());
		Lunch lunch = new Lunch(lunchService);
		lunch.launch();
	}
}
