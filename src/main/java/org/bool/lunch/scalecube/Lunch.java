package org.bool.lunch.scalecube;

import org.bool.lunch.CachedRunnerFactory;
import org.bool.lunch.DefaultRunnerFactory;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.PidReader;
import org.bool.lunch.RunnerType;

import io.scalecube.services.Microservices;
import io.scalecube.services.ServiceEndpoint;
import io.scalecube.services.ServiceInfo;
import io.scalecube.services.discovery.ScalecubeServiceDiscovery;
import io.scalecube.services.discovery.api.ServiceDiscovery;
import io.scalecube.services.gateway.Gateway;
import io.scalecube.services.gateway.GatewayOptions;
import io.scalecube.services.gateway.http.HttpGateway;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Lunch {

	private final LunchService service;
	
	private final int port;
	
	public Lunch(LunchService service) {
		this(service, 0);
	}
	
	public Lunch(LunchService service, int port) {
		this.service = service;
		this.port = port;
	}

	public Mono<Microservices> launch() {
		return Microservices.builder()
				.services(ServiceInfo.fromServiceInstance(service)
						.tag("role", "seed")
						.build())
				.gateway(this::gateway)
				.discovery(this::discovery)
				.start();
	}
	
	private Gateway gateway(GatewayOptions options) {
	    return new HttpGateway(options.port(port));
	}
		
	private ServiceDiscovery discovery(ServiceEndpoint endpoint) {
		return new ScalecubeServiceDiscovery(endpoint);
	}
	
	public static void main(String[] args) {
		CachedRunnerFactory<String> factory = new CachedRunnerFactory<>(new DefaultRunnerFactory(), RunnerType::valueOf);
		LunchRunner runner = new LunchRunner(factory::lookup, PidReader.DEFAULT);
		Luncher luncher = new Luncher(runner, Schedulers.newElastic("local-lunch"));
		LocalLunchService lunchService = new LocalLunchService(luncher);
		Lunch lunch = new Lunch(lunchService, args.length > 0 ? Integer.parseInt(args[0]) : 0);
		lunch.launch()
				.flatMap(Microservices::onShutdown).block();
	}
}
