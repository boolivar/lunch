package org.bool.lunch.core;

import org.bool.lunch.api.LunchedItem;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.concurrent.CompletableFuture;
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

	@Test
	void testTerminate() {
		var processHandle = mock(ProcessHandle.class);
		given(process.descendants())
			.willReturn(Stream.of(processHandle));

		var mono = lunched.terminate(false);
		verifyNoInteractions(processHandle);

		StepVerifier.create(mono)
			.verifyComplete();
		verify(processHandle).destroy();
	}

	@Test
	void testForceTerminate() {
		var processHandle = mock(ProcessHandle.class);
		given(process.descendants())
			.willReturn(Stream.of(processHandle));

		var mono = lunched.terminate(true);
		verifyNoInteractions(processHandle);

		StepVerifier.create(mono)
			.expectSubscription()
			.verifyComplete();
		verify(processHandle).destroyForcibly();
	}
}
