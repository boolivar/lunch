package org.bool.lunch.scalecube.gateway;

import java.util.Map;

public class NodeInfo {

	private final String id;
	
	private final String address;
	
	private final Map<String, String> tags;
	
	private final Object data;
	
	public NodeInfo(String id, String address, Map<String, String> tags, Object data) {
		this.id = id;
		this.address = address;
		this.tags = tags;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public String getAddress() {
		return address;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public Object getData() {
		return data;
	}
}
