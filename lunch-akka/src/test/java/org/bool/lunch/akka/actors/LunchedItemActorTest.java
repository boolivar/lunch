package org.bool.lunch.akka.actors;

import org.bool.lunch.api.LunchedItem;

import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.Behaviors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import reactor.core.publisher.Mono;
import reactor.test.publisher.TestPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class LunchedItemActorTest {

	private final TestPublisher<Integer> exitCode = TestPublisher.create();

	private final LunchedItem item = mock(LunchedItem.class);

	private BehaviorTestKit<LunchedItemCommand> actor;

	@BeforeEach
	void setupActor() {
		when(item.exitCode())
			.thenReturn(Mono.from(exitCode));
		actor = BehaviorTestKit.create(LunchedItemActor.create(item));
	}

	@AfterEach
	void completePublisher() {
		exitCode.complete();
	}

	@ValueSource(booleans = { true, false })
	@ParameterizedTest
	void testTerminate(boolean force) {
		var publisher = TestPublisher.<Void>create();
		given(item.terminate(force))
			.willReturn(Mono.from(publisher));

		actor.run(new LunchedItemCommand.Terminate(force));

		publisher.assertSubscribers(1);
		assertThat(actor.returnedBehavior())
			.isEqualTo(actor.currentBehavior());
	}

	@Test
	void testTerminated() {
		actor.run(new LunchedItemCommand.Terminated(42));

		assertThat(actor.returnedBehavior())
			.isEqualTo(Behaviors.stopped());
	}

	@Test
	void testProcessTerminated() {
		exitCode.emit(3);

		assertThat(actor.selfInbox().getAllReceived())
			.containsOnly(new LunchedItemCommand.Terminated(3));
	}

	@Test
	void testPostStopAlive() {
		var publisher = TestPublisher.<Void>create();
		given(item.isAlive())
			.willReturn(true);
		given(item.terminate(true))
			.willReturn(Mono.from(publisher));

		actor.signal(PostStop.instance());

		publisher.assertSubscribers(1);
	}

	@Test
	void testPostStopTerminated() {
		given(item.isAlive())
			.willReturn(false);

		actor.signal(PostStop.instance());

		then(item).should(never()).terminate(any());
	}
}
