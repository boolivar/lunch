package org.bool.lunch.core;

import org.bool.lunch.api.LunchedItem;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class LocalProcessLunchedTest {

	private final Process process = mock(Process.class);

	private final LunchedItem lunched = new LocalProcessLunched("test", process);

	@Test
	void testFields() {
		given(process.pid())
			.willReturn(777L);
		assertThat(lunched)
			.returns("test", LunchedItem::getName)
			.returns("777", LunchedItem::getPid)
			;
	}

	@Test
	void testExitCode() {
		given(process.onExit())
			.willReturn(CompletableFuture.completedFuture(process));
		given(process.exitValue())
			.willReturn(88);

		StepVerifier.create(lunched.exitCode())
			.expectNext(88)
			.verifyComplete();
	}

	@MethodSource
	@ParameterizedTest
	void testTerminate(boolean force, Consumer<ProcessHandle> destroyMethod) {
		var processHandle = mock(ProcessHandle.class);
		var childHandle = mock(ProcessHandle.class);

		given(process.toHandle())
			.willReturn(processHandle);
		given(process.descendants())
			.willReturn(Stream.of(childHandle));

		var mono = lunched.terminate(force);
		verifyNoInteractions(processHandle);

		StepVerifier.create(mono)
			.expectSubscription()
			.verifyComplete();

		destroyMethod.accept(verify(processHandle));
		destroyMethod.accept(verify(childHandle));
	}

	static Stream<Arguments> testTerminate() {
		return Stream.of(
			Arguments.of(false, namedMethod("destroy", ProcessHandle::destroy)),
			Arguments.of(true, namedMethod("destroyForcibly", ProcessHandle::destroyForcibly))
		);
	}

	private static <T> Named<Consumer<T>> namedMethod(String name, Consumer<T> methodRef) {
		return Named.of(name, methodRef);
	}
}
