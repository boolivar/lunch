package org.bool.lunch.core;

import org.bool.lunch.LunchItem;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessBuilderMapperTest {

	private final ProcessBuilderMapper mapper = new ProcessBuilderMapper();

	@Test
	void testSingleCommandMapping() {
		var item = LunchItem.builder()
			.command("tst")
			.build();

		var builder = mapper.map(item);

		assertThat(builder)
			.returns(List.of("tst"), ProcessBuilder::command)
			.returns(true, ProcessBuilder::redirectErrorStream);
	}

	@Test
	void testMapping() {
		var item = LunchItem.builder()
			.command("cmd")
			.args(List.of("A", "b", "-"))
			.env(Map.of("K", "v"))
			.workDir(new File("wd"))
			.logFile(new File("log"))
			.build();

		var builder = mapper.map(item);

		assertThat(builder)
			.returns(List.of("cmd", "A", "b", "-"), ProcessBuilder::command)
			.returns(true, ProcessBuilder::redirectErrorStream)
			.returns(new File("wd"), ProcessBuilder::directory)
			.returns(Redirect.to(new File("log")), ProcessBuilder::redirectOutput)
			.extracting(ProcessBuilder::environment, InstanceOfAssertFactories.MAP)
				.containsEntry("K", "v")
			;
	}
}
