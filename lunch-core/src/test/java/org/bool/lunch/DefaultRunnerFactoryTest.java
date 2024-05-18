package org.bool.lunch;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DefaultRunnerFactoryTest {

	private DefaultRunnerFactory factory = new DefaultRunnerFactory();

	@Test
	void testJavaRunner() {
		Runner runner = factory.create("JAVA");
		assertThat(runner)
			.isInstanceOf(JavaProcessRunner.class);
	}
}
