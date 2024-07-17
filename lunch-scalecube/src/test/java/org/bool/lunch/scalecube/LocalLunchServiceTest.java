package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.api.LunchedItem;
import org.bool.lunch.api.Luncher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocalLunchServiceTest {

	@Mock
	private Luncher luncher;

	@Mock
	private Map<String, LunchedItem> lunchedMap;

	@InjectMocks
	private LocalLunchService service;

	@Test
	void testLaunch(@Mock LunchedItem lunched) {
		var item = LunchItem.builder().name("test-cmd").type("test").command("cmd").args(List.of()).build();

		given(luncher.launch(item))
			.willReturn((Mono) Mono.just(lunched));
		given(lunched.getName())
			.willReturn("test");
		given(lunched.getPid())
			.willReturn("test-pid");
		given(lunched.exitCode())
			.willReturn(Mono.just(44));

		var result = service.launch(item);
		verifyNoInteractions(lunched);

		StepVerifier.create(result)
			.expectNext(new LunchInfo("test-pid", "test", null, 44))
			.expectComplete()
			.verify()
			;
	}

	@Test
	void testLand(@Mock LunchedItem item) {
		given(item.getName())
			.willReturn("test");
		given(item.getPid())
			.willReturn("id");
		given(item.exitCode())
			.willReturn(Mono.just(5));

		given(item.terminate(false))
			.willReturn(Mono.empty());
		given(lunchedMap.get("pid"))
			.willReturn(item);

		var result = service.land("pid");
		verifyNoInteractions(item);

		StepVerifier.create(result)
			.expectNext(new LunchInfo("id", "test", null, 5))
			.expectComplete()
			.verify();
	}
}
