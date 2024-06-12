package org.bool.lunch.akka.actors;

import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import akka.actor.testkit.typed.javadsl.TestInbox;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LunchStatusRequestActorTest {

	private final TestInbox<ClusterStatusRequestCommand.LunchStatus> replyInbox = TestInbox.create();

	private final TestInbox<LunchedItemCommand> lunchedInbox = TestInbox.create();

	@Test
	void testEmptyLunch() {
		var actor = BehaviorTestKit.create(LunchStatusRequestActor.create(replyInbox.getRef(), List.of()));

		replyInbox.expectMessage(new ClusterStatusRequestCommand.LunchStatus(actor.getRef().path().parent().toString(), List.of()));
		assertThat(actor.currentBehavior())
			.isEqualTo(Behaviors.stopped());
	}

	@Test
	void testStatusRequest() {
		var response = new StatusResponse("test", "pid", "info");
		var actor = BehaviorTestKit.create(LunchStatusRequestActor.create(replyInbox.getRef(), List.of(lunchedInbox.getRef())));

		actor.run(new LunchStatusRequestCommand.Status(response));

		replyInbox.expectMessage(new ClusterStatusRequestCommand.LunchStatus(actor.getRef().path().parent().toString(), List.of(response)));
		assertThat(actor.currentBehavior())
			.isEqualTo(Behaviors.stopped());
	}

	@Test
	void testTerminated() {
		var actor = BehaviorTestKit.create(LunchStatusRequestActor.create(replyInbox.getRef(), List.of(lunchedInbox.getRef())));

		actor.signal(new Terminated(lunchedInbox.getRef().unsafeUpcast()));

		replyInbox.expectMessage(new ClusterStatusRequestCommand.LunchStatus(actor.getRef().path().parent().toString(), List.of()));
		assertThat(actor.currentBehavior())
			.isEqualTo(Behaviors.stopped());
	}
}
