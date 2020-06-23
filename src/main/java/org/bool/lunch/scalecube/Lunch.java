package org.bool.lunch.scalecube;

import org.bool.lunch.CachedRunnerFactory;
import org.bool.lunch.DefaultRunnerFactory;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.PidReader;
import org.bool.lunch.RunnerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import io.scalecube.cluster.membership.MembershipConfig;
import io.scalecube.net.Address;
import io.scalecube.services.Microservices;
import io.scalecube.services.ServiceEndpoint;
import io.scalecube.services.discovery.ScalecubeServiceDiscovery;
import io.scalecube.services.discovery.api.ServiceDiscovery;
import io.scalecube.services.discovery.api.ServiceDiscoveryEvent;
import io.scalecube.services.gateway.Gateway;
import io.scalecube.services.gateway.GatewayOptions;
import io.scalecube.services.gateway.http.HttpGateway;
import io.scalecube.services.transport.rsocket.RSocketServiceTransport;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Lunch {
	
	private static final Logger log = LoggerFactory.getLogger(Lunch.class);

	private final LunchService service;
	
	private final int gatewayPort;
	
	private final Address seed;
	
	public Lunch(LunchService service, int gatewayPort, Address seed) {
		this.service = service;
		this.gatewayPort = gatewayPort;
		this.seed = seed;
	}
	
	public Mono<Microservices> launch() {
		return Microservices.builder()
				.services(service)
				.gateway(this::gateway)
				.discovery(this::discovery)
				.transport(RSocketServiceTransport::new)
				.tags(Collections.singletonMap("role", seed != null ? "member" : "seed"))
				.start()
				.doOnNext(m -> m.discovery().listenDiscovery().subscribe(e -> handleClusterEvent(m, e)))
				;
	}
	
	private Gateway gateway(GatewayOptions options) {
		return new HttpGateway(options.port(gatewayPort).call(options.call().methodRegistry(null)));
	}
		
	private ServiceDiscovery discovery(ServiceEndpoint endpoint) {
		return new ScalecubeServiceDiscovery(endpoint)
				.options(clusterConfig -> clusterConfig.membership(this::membership));
	}
	
	private MembershipConfig membership(MembershipConfig config) {
		if (seed != null) {
			return config.seedMembers(seed);
		}
		return config;
	}
	
	private void handleClusterEvent(Microservices microservices, ServiceDiscoveryEvent event) {
		log.info("Received cluster event: {}", event);
		if (event.isEndpointRemoved() && "seed".equals(event.serviceEndpoint().tags().get("role"))) {
			log.info("Shutdown service on seed removal");
			microservices.shutdown().subscribe();
		}
	}
	
	public static void main(String[] args) {
		CachedRunnerFactory<String> factory = new CachedRunnerFactory<>(new DefaultRunnerFactory(), RunnerType::valueOf);
		LunchRunner runner = new LunchRunner(factory::lookup, PidReader.DEFAULT);
		Luncher luncher = new Luncher(runner, Schedulers.newElastic("local-lunch"));
		LocalLunchService lunchService = new LocalLunchService(luncher);
		
		int gatewayPort = args.length > 0 ? Integer.parseInt(args[0]) : 0;
		Address seed = args.length > 1 ? Address.from(args[1]) : null;
		
		Lunch lunch = new Lunch(lunchService, gatewayPort, seed);
		Microservices microservices = lunch.launch().block();
		
		log.info("Service discovery: {}", microservices.discovery().address());
		
		microservices.onShutdown().block();
	}
}
