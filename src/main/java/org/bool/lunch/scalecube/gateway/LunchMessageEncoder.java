package org.bool.lunch.scalecube.gateway;

import java.io.IOException;
import java.util.function.BiConsumer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.scalecube.services.api.ServiceMessage;
import io.scalecube.services.transport.api.DataCodec;

public class LunchMessageEncoder implements BiConsumer<ServiceMessage, ByteBuf> {

	@Override
	public void accept(ServiceMessage message, ByteBuf buffer) {
		DataCodec codec = DataCodec.getInstance(message.dataFormatOrDefault());
		try {
			codec.encode(new ByteBufOutputStream(buffer), message.data());
		} catch (IOException e) {
			throw new RuntimeException("Error encode message " + message, e);
		}
	}
}
