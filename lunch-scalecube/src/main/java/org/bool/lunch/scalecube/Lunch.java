package org.bool.lunch.scalecube;

import org.bool.lunch.core.LocalProcessLuncher;
import org.bool.lunch.scalecube.gateway.LunchHttpGateway;
import org.bool.lunch.scalecube.gateway.LunchHttpHandler;
import org.bool.lunch.scalecube.gateway.LunchHttpServer;
import org.bool.lunch.scalecube.gateway.LunchMessageProcessor;

import io.scalecube.cluster.membership.MembershipConfig;
import io.scalecube.net.Address;
import io.scalecube.services.Microservices;
import io.scalecube.services.ServiceEndpoint;
import io.scalecube.services.discovery.ScalecubeServiceDiscovery;
import io.scalecube.services.discovery.api.ServiceDiscovery;
import io.scalecube.services.discovery.api.ServiceDiscoveryEvent;
import io.scalecube.services.gateway.Gateway;
import io.scalecube.services.gateway.GatewayOptions;
import io.scalecube.services.registry.api.ServiceRegistry;
import io.scalecube.services.transport.api.DataCodec;
import io.scalecube.services.transport.rsocket.RSocketServiceTransport;
import io.scalecube.transport.netty.tcp.TcpTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Collections;

@SuppressWarnings("PMD")
public class Lunch {
	
	private static final Logger log = LoggerFactory.getLogger(Lunch.class);
	
	private static final String CONTENT_TYPE = "application/json";
	
	private static final String ROLE_KEY = "role";
	
	private static final String NODE_KEY = "node";
	
	private static final String MASTER_TAG = "master";
	
	private static final String WORKER_TAG = "worker";

	private final LunchService service;
	
	private final int gatewayPort;
	
	private final Address seed;

	private final ServiceRegistry serviceRegistry;

	public Lunch(LunchService service, int gatewayPort, Address seed, ServiceRegistry serviceRegistry) {
		this.service = service;
		this.gatewayPort = gatewayPort;
		this.seed = seed;
		this.serviceRegistry = serviceRegistry;
	}
	
	public Mono<Microservices> launch() {
		return Microservices.builder()
				.services(service)
				.gateway(this::gateway)
				.discovery(this::discovery)
				.serviceRegistry(serviceRegistry)
				.transport(RSocketServiceTransport::new)
				.tags(Collections.singletonMap(ROLE_KEY, seed == null ? MASTER_TAG : WORKER_TAG))
				.start()
				;
	}
	
	private Gateway gateway(GatewayOptions options) {
		LunchMessageProcessor messageProcessor = new LunchMessageProcessor(options.call().methodRegistry(null), serviceRegistry);
		LunchHttpHandler handler = new LunchHttpHandler(messageProcessor, DataCodec.getInstance(CONTENT_TYPE));
		LunchHttpServer httpServer = new LunchHttpServer("localhost", gatewayPort, handler);
		return new LunchHttpGateway(options, httpServer);
	}

	private ServiceDiscovery discovery(ServiceEndpoint endpoint) {
		return new ScalecubeServiceDiscovery()
				.transport(transportConfig -> transportConfig.transportFactory(new TcpTransportFactory()))
				.options(clusterConfig -> clusterConfig.membership(this::membership))
				;
	}
	
	private MembershipConfig membership(MembershipConfig config) {
		if (seed != null) {
			return config.seedMembers(seed);
		}
		return config;
	}
	
	public static void main(String[] args) {
		LocalProcessLuncher luncher = new LocalProcessLuncher();
		LocalLunchService lunchService = new LocalLunchService(luncher);
		
		int gatewayPort = args.length > 0 ? Integer.parseInt(args[0]) : 0;
		Address seed = args.length > 1 ? Address.from(args[1]) : null;
		
		ServiceFilter serviceFilter = new ServiceFilterFactory()
				.withQualifier()
				.withContentType()
				.withTag(ROLE_KEY)
				.withId(NODE_KEY)
				.create();
		LunchServiceRegistry serviceRegistry = new LunchServiceRegistry(serviceFilter);
		
		Lunch lunch = new Lunch(lunchService, gatewayPort, seed, serviceRegistry);
		
		try (Microservices microservices = lunch.launch().blockOptional().orElseThrow()) {
			log.info("Service discovery: {}", microservices.discoveryAddress());
			serviceRegistry.registerService(microservices.serviceEndpoint());
			microservices.listenDiscovery().subscribe(event -> handleClusterEvent(microservices, event));
			microservices.onShutdown().block();
		}
	}
	
	private static void handleClusterEvent(Microservices microservices, ServiceDiscoveryEvent event) {
		log.info("Received cluster event: {}", event);
		if (event.isEndpointRemoved() && MASTER_TAG.equals(event.serviceEndpoint().tags().get(ROLE_KEY))) {
			log.info("Shutdown service on seed removal");
			microservices.shutdown().subscribe();
		}
	}
}
