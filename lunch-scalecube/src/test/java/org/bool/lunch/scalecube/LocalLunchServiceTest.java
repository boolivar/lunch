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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
		given(lunched.getPid())
			.willReturn("test-pid");
		given(lunched.exitCode())
			.willReturn(Mono.just(44));

		var result = service.launch(item);

		assertThat(result.block())
			.extracting(LunchInfo::getPid, LunchInfo::getExitCode)
			.containsExactly("test-pid", 44)
			;
	}
}
