package org.bool.lunch.akka.actors;

import org.bool.lunch.LunchItem;
import org.bool.lunch.api.LunchedItem;
import org.bool.lunch.api.Luncher;

import akka.actor.testkit.typed.Effect;
import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class LunchActorTest {

	private final Luncher luncher = mock(Luncher.class);

	private final BehaviorTestKit<LunchCommand> actor = BehaviorTestKit.create(LunchActor.create(luncher));

	@Test
	void testLaunch() {
		var item = new LunchItem();
		var lunched = mock(LunchedItem.class);

		given(luncher.launch(item))
			.willReturn((Mono) Mono.just(lunched));

		actor.run(new LunchCommand.Launch(item));

		assertThat(actor.selfInbox().getAllReceived())
			.containsOnly(new LunchCommand.Lunched(lunched));
	}

	@Test
	void testLanded() {
		var itemName = "test";
		var lunched = mock(LunchedItem.class);
		given(lunched.getName())
			.willReturn(itemName);
		given(lunched.exitCode())
			.willReturn(Mono.empty());

		actor.run(new LunchCommand.Lunched(lunched));
		actor.run(new LunchCommand.Land(itemName));

		assertThat(actor.expectEffectClass(Effect.Spawned.class).childName())
			.isEqualTo(itemName);
		assertThat(actor.childInbox(itemName).getAllReceived())
			.containsOnly(new LunchedItemCommand.Terminate(false));
	}
}
