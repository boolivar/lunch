package org.bool.lunch.config;

import org.bool.lunch.Lunch;
import org.bool.lunch.LunchItem;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class YamlConfigReaderTest {

	private final YamlConfigReader yamlReader = new YamlConfigReader();

	@Test
	void testParseYaml() {
		var yaml = """
			name: test
			command: echo
			args:
			- 0
			- zero
			env:
			  A: 0
			  B: value
			""";
		var expectedValue = LunchItem.builder()
				.name("test")
				.command("echo")
				.args(List.of("0", "zero"))
				.env(Map.of("A", "0", "B", "value"))
				.build();

		assertThat(yamlReader.read(() -> new StringReader(yaml), LunchItem.class).block())
			.isEqualTo(expectedValue);
	}

	@Test
	void testDeepItemsRoundtrip() throws IOException {
		var yaml = """
			items:
			- name: zookeeper
			  command: org.apache.zookeeper.server.quorum.QuorumPeerMain
			  args:
			  - c:\\bin\\kafka_2.11-1.0.0\\config\\zookeeper.properties
			- name: kafka
			  command: kafka.Kafka
			  args:
			  - c:\\bin\\kafka_2.11-1.0.0\\config\\server.properties
			- name: lunch
			  command: java -jar lunch.jar
			  items:
			    - name: lunch
			      command: java -jar lunch.jar
			      items:
			        - name: kibana
			          command: c:\\bin\\kibana-7.17.21\\bin\\kibana.bat
			          workDir: c:\\bin\\kibana-7.17.21\\
			    - name: elasticsearch
			      command: c:\\bin\\elasticsearch-7.17.3\\bin\\elasticsearch.bat
			      workDir: c:\\bin\\elasticsearch-7.17.3\\bin
			""";
		var lunch = yamlReader.read(() -> new StringReader(yaml), Lunch.class).block();
		var items = lunch.getItems().get(2).getItems();
		var subYaml = yamlReader.toYamlString(new Lunch(items)).block();

		assertThat(yamlReader.read(() -> new StringReader(subYaml), Lunch.class).block())
			.isEqualTo(new Lunch(items));
	}
}
