package org.bool.lunch;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaProcessRunnerTest {

	private static final String JAVA_BIN = "/bin/java/exe";
	
	private static final String CLASSPATH = ".:/usr/share/libs";
	
	private static final List<String> JAVA_ARGS = Collections.singletonList("-Djava.class.path=/usr/jvm/libs"); 
	
	private final JavaProcessRunner jpr = new JavaProcessRunner(new TestProcessRunner(), JAVA_BIN, CLASSPATH, JAVA_ARGS);
	
	@Test
	public void testRunner() {
		TestProcess process = (TestProcess) jpr.run("java.util.List", Arrays.asList("-version"));
		assertEquals(JAVA_BIN, process.getCommand());
		ArrayDeque<String> args = new ArrayDeque<>(process.getArgs());
		assertEquals(JAVA_ARGS.get(0), args.pop());
		assertEquals("-cp", args.pop());
		assertEquals(CLASSPATH, args.pop());
		assertEquals("java.util.List", args.pop());
		assertEquals("-version", args.pop());
	}
	
	private static class TestProcessRunner implements Runner {
		@Override
		public Process run(String command, Collection<String> args) {
			return new TestProcess(command, args);
		}
	}
	
	static class TestProcess extends Process {
		
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
		public OutputStream getOutputStream() {
			return null;
		}

		@Override
		public InputStream getInputStream() {
			return null;
		}

		@Override
		public InputStream getErrorStream() {
			return null;
		}

		@Override
		public int waitFor() throws InterruptedException {
			return 0;
		}

		@Override
		public int exitValue() {
			return 0;
		}

		@Override
		public void destroy() {
		}
	}
}
