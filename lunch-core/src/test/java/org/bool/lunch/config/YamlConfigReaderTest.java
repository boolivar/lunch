package org.bool.lunch.config;

import org.bool.lunch.LunchItem;

import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;

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
			""";
		assertThat(yamlReader.read(() -> new StringReader(yaml), LunchItem.class).block())
			.isEqualTo(new LunchItem("test", null, "echo", List.of("0", "zero")));
	}
}
