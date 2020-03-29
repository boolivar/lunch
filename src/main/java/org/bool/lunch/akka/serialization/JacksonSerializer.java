package org.bool.lunch.akka.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

import akka.serialization.JSerializer;

public class JacksonSerializer extends JSerializer implements Serializable {

	private static final long serialVersionUID = 6239254260900626L;
	
	private static final ObjectMapper jsonMapper = new ObjectMapper();

	@Override
	public int identifier() {
		return (int) serialVersionUID;
	}

	@Override
	public boolean includeManifest() {
		return true;
	}

	@Override
	public byte[] toBinary(Object object) {
		try {
			return jsonMapper.writeValueAsBytes(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Fail to serialize instance: " + object, e);
		}
	}

	@Override
	public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
		try {
			return jsonMapper.readValue(bytes, manifest);
		} catch (IOException e) {
			throw new RuntimeException("Fail to read instance of " + manifest, e);
		}
	}
}