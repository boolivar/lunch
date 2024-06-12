package org.bool.lunch.akka.actors;

import akka.actor.testkit.typed.javadsl.BehaviorTestKit;
import akka.actor.testkit.typed.javadsl.TestInbox;
import akka.actor.typed.Terminated;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.receptionist.Receptionist.Listing;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ClusterStatusRequestActorTest {

	private final Listing listing = mock(Listing.class);

	private final TestInbox<ClusterStatusResponse> inbox = TestInbox.create();

	private final BehaviorTestKit<ClusterStatusRequestCommand> actor = BehaviorTestKit.create(ClusterStatusRequestActor.create(inbox.getRef()));

	@Test
	void testEmptyListing() {
		given(listing.getServiceInstances(LunchActor.SERVICE_KEY))
			.willReturn(Set.of());

		actor.run(new ClusterStatusRequestCommand.ClusterListing(listing));

		inbox.expectMessage(new ClusterStatusResponse(Map.of()));

		assertThat(actor.returnedBehavior())
			.isSameAs(Behaviors.stopped());
	}

	@Test
	void testRequests() {
		var response = new StatusResponse("test", "32", "desc");
		TestInbox<LunchCommand> lunch = TestInbox.create();
		given(listing.getServiceInstances(LunchActor.SERVICE_KEY))
			.willReturn(Set.of(lunch.getRef()));

		actor.run(new ClusterStatusRequestCommand.ClusterListing(listing));

		lunch.expectMessage(new LunchCommand.Status(actor.getRef().narrow()));

		actor.run(new ClusterStatusRequestCommand.LunchStatus("test-lunch", List.of(response)));

		inbox.expectMessage(new ClusterStatusResponse(Map.of("test-lunch", List.of(response))));

		assertThat(actor.returnedBehavior())
			.isSameAs(Behaviors.stopped());
	}

	@Test
	void testTerminatedSignal() {
		actor.signal(new Terminated(TestInbox.create().getRef().narrow()));

		inbox.expectMessage(new ClusterStatusResponse(Map.of()));
		assertThat(actor.returnedBehavior())
			.isSameAs(Behaviors.stopped());
	}

	@Test
	void testExpiry() {
		actor.run(ClusterStatusRequestCommand.Timeout.EXPIRED);

		inbox.expectMessage(new ClusterStatusResponse(Map.of()));
		assertThat(actor.returnedBehavior())
			.isSameAs(Behaviors.stopped());
	}
}
