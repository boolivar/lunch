package org.bool.lunch.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LunchedItemTest {

	@Spy
	private LunchedItem item;

	static Stream<Mono<Integer>> testTerminated() {
		return Stream.of(Mono.empty(), Mono.just(0), Mono.just(-1), Mono.error(new RuntimeException("TEST")));
	}

	@MethodSource
	@ParameterizedTest
	void testTerminated(Mono<Integer> exitCode) {
		given(item.exitCode())
			.willReturn(exitCode);
		assertThat(item.isAlive())
			.isFalse();
	}

	@Test
	void testAlive() {
		given(item.exitCode())
			.willReturn(Mono.never());
		assertThat(item.isAlive())
			.isTrue();
	}
}
