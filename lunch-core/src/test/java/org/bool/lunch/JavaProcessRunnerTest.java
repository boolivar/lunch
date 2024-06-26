package org.bool.lunch;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JavaProcessRunnerTest {

	private static final String JAVA_BIN = "/bin/java/exe";
	
	private static final String CLASSPATH = ".:/usr/share/libs";
	
	private static final List<String> JAVA_ARGS = Collections.singletonList("-Djava.class.path=/usr/jvm/libs"); 
	
	private final JavaProcessRunner jpr = new JavaProcessRunner(new TestProcessRunner(), JAVA_BIN, CLASSPATH);
	
	@Test
	void testSingleCommand() {
		var process = (TestProcess) jpr.run("java.util.List", Collections.emptyList());
		assertEquals(JAVA_BIN, process.getCommand());
		var args = new ArrayDeque<>(process.getArgs());
		assertEquals("-cp", args.pop());
		assertEquals(CLASSPATH, args.pop());
		assertEquals("java.util.List", args.pop());
		assertTrue(args.isEmpty());
	}
	
	@Test
	void testRunner() {
		var process = (TestProcess) jpr.run("java.util.List -version", JAVA_ARGS);
		assertEquals(JAVA_BIN, process.getCommand());
		var args = new ArrayDeque<>(process.getArgs());
		assertEquals(JAVA_ARGS.get(0), args.pop());
		assertEquals("-cp", args.pop());
		assertEquals(CLASSPATH, args.pop());
		assertEquals("java.util.List", args.pop());
		assertEquals("-version", args.pop());
		assertTrue(args.isEmpty());
	}

	static class TestProcessRunner implements Runner {
		@Override
		public LunchProcess run(String command, Collection<String> args) {
			return new TestProcess(command, args);
		}
	}

	static class TestProcess implements LunchProcess {
		
		private final String command;
		
		private final Collection<String> args;
		
		TestProcess(String command, Collection<String> args) {
			this.command = command;
			this.args = args;
		}
		
		public String getCommand() {
			return command;
		}

		public Collection<String> getArgs() {
			return args;
		}

		@Override
		public String getPid() {
			return null;
		}

		@Override
		public Integer waitFor(Duration duration) {
			return null;
		}

		@Override
		public void destroy() {
		}
	}
}
