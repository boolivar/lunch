package org.bool.lunch.config;

import org.bool.lunch.Lunch;
import org.bool.lunch.LunchItem;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
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
	void testDeepItems() throws IOException {
		var yaml = IOUtils.resourceToString("/deep-config.yml", StandardCharsets.UTF_8);
		var lunch = yamlReader.read(() -> new StringReader(yaml), Lunch.class).block();
		var args = lunch.getItems().get(0).getArgs();
		assertThat(args)
			.hasSize(1);

		var level1 = yamlReader.read(() -> new StringReader(args.get(0)), Lunch.class).block();
		assertThat(level1.getItems())
			.hasSize(2)
			.extracting(LunchItem::getName)
			.containsExactly("lunch", "elasticsearch");
		assertThat(level1.getItems().get(0).getArgs())
			.hasSize(1);

		var level2 = yamlReader.read(() -> new StringReader(level1.getItems().get(0).getArgs().get(0)), Lunch.class).block();
		assertThat(level2.getItems())
			.singleElement()
			.returns("kibana", LunchItem::getName)
			;
	}
}
