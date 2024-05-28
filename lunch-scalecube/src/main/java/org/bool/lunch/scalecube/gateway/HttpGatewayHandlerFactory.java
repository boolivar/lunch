package org.bool.lunch.scalecube.gateway;

import io.scalecube.services.ServiceCall;
import org.reactivestreams.Publisher;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.function.BiFunction;

@FunctionalInterface
public interface HttpGatewayHandlerFactory {
	BiFunction<? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>> create(ServiceCall serviceCall);
}
