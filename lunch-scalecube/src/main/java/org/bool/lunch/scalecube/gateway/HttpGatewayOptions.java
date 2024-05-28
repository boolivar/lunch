package org.bool.lunch.scalecube.gateway;

import io.scalecube.services.ServiceCall;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class HttpGatewayOptions {

	private String id;

	@NonNull
	@Builder.Default
	private String host = "localhost";

	private int port;

	@NonNull
	private ServiceCall serviceCall;

}
