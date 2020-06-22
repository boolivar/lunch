package org.bool.lunch.scalecube;

import org.bool.lunch.CachedRunnerFactory;
import org.bool.lunch.DefaultRunnerFactory;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.PidReader;
import org.bool.lunch.RunnerType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.scalecube.cluster.membership.MembershipConfig;
import io.scalecube.net.Address;
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
	
	private final List<String> seeds;
	
	private final int gatewayPort;
	
	public Lunch(LunchService service, List<String> seeds, int gatewayPort) {
		this.service = service;
		this.seeds = seeds;
		this.gatewayPort = gatewayPort;
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
		return new HttpGateway(options.port(gatewayPort));
	}
		
	private ServiceDiscovery discovery(ServiceEndpoint endpoint) {
		return new ScalecubeServiceDiscovery(endpoint)
				.options(clusterConfig -> clusterConfig.membership(this::membership));
	}
	
	private MembershipConfig membership(MembershipConfig config) {
		if (seeds == null || seeds.isEmpty()) {
			return config;
		}
		return config.seedMembers(seeds.stream().map(Address::from).collect(Collectors.toList()));
	}
	
	public static void main(String[] args) {
		CachedRunnerFactory<String> factory = new CachedRunnerFactory<>(new DefaultRunnerFactory(), RunnerType::valueOf);
		LunchRunner runner = new LunchRunner(factory::lookup, PidReader.DEFAULT);
		Luncher luncher = new Luncher(runner, Schedulers.newElastic("local-lunch"));
		LocalLunchService lunchService = new LocalLunchService(luncher);
		
		int gatewayPort = args.length > 0 ? Integer.parseInt(args[0]) : 0;
		List<String> seeds = args.length > 1 ? Arrays.asList(args).subList(1, args.length) : Collections.emptyList();
		
		Lunch lunch = new Lunch(lunchService, seeds, gatewayPort);
		lunch.launch()
				.flatMap(Microservices::onShutdown).block();
	}
}
