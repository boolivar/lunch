package org.bool.lunch.scalecube;

import org.bool.lunch.core.LocalProcessLuncher;
import org.bool.lunch.scalecube.gateway.HttpGateway;
import org.bool.lunch.scalecube.gateway.NettyHttpGatewayServerFactory;

import io.scalecube.cluster.membership.MembershipConfig;
import io.scalecube.net.Address;
import io.scalecube.services.Microservices;
import io.scalecube.services.discovery.ScalecubeServiceDiscovery;
import io.scalecube.services.discovery.api.ServiceDiscovery;
import io.scalecube.services.discovery.api.ServiceDiscoveryEvent;
import io.scalecube.services.registry.api.ServiceRegistry;
import io.scalecube.services.transport.rsocket.RSocketServiceTransport;
import io.scalecube.transport.netty.tcp.TcpTransportFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
public class Lunch {

	private static final String ROLE_KEY = "role";

	private static final String NODE_KEY = "node";

	private static final String MASTER_TAG = "master";

	private final LunchService service;

	private final String host;

	private final int port;

	private final String role;

	private final ServiceRegistry serviceRegistry;

	public Lunch(LunchService service, String host, int port, String role, ServiceRegistry serviceRegistry) {
		this.service = service;
		this.host = host;
		this.port = port;
		this.role = role;
		this.serviceRegistry = serviceRegistry;
	}
	
	public Mono<Microservices> launch() {
		var microservices = Microservices.builder()
				.services(service)
				.discovery(serviceEndpoint -> discovery())
				.serviceRegistry(serviceRegistry)
				.transport(RSocketServiceTransport::new)
				.tags(Collections.singletonMap(ROLE_KEY, role))
				;
		if (MASTER_TAG.equalsIgnoreCase(role)) {
			microservices.gateway(options -> new HttpGateway(options.id("http").port(port), host, new NettyHttpGatewayServerFactory()));
		}
		return microservices.start();
	}

	private ServiceDiscovery discovery() {
		return new ScalecubeServiceDiscovery()
				.transport(transportConfig -> transportConfig.transportFactory(new TcpTransportFactory()))
				.options(clusterConfig -> clusterConfig.membership(this::membership))
				;
	}

	private MembershipConfig membership(MembershipConfig config) {
		return MASTER_TAG.equalsIgnoreCase(role) ? config : config.seedMembers(Address.create(host, port));
	}

	public static void main(String[] args) {
		LocalProcessLuncher luncher = new LocalProcessLuncher();
		LocalLunchService lunchService = new LocalLunchService(luncher);

		var address = args.length > 0 ? StringUtils.split(args[0], ':') : new String[] {"0"};
		var host = address.length > 1 ? address[0] : "localhost";
		var port = Integer.parseInt(address.length == 1 ? address[0] : address[1]);
		var role = args.length > 1 ? args[1] : MASTER_TAG;

		ServiceFilter serviceFilter = new ServiceFilterFactory()
				.withQualifier()
				.withContentType()
				.withTag(ROLE_KEY)
				.withId(NODE_KEY)
				.create();
		LunchServiceRegistry serviceRegistry = new LunchServiceRegistry(serviceFilter);

		Lunch lunch = new Lunch(lunchService, host, port, role, serviceRegistry);
		
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
