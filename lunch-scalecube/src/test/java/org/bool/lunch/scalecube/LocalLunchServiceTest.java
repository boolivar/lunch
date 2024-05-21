package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.LunchProcess;
import org.bool.lunch.Lunched;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocalLunchServiceTest {

	@Mock
	private Supplier<Lunched> supplier;

	@Mock
	private Luncher luncher;

	@Mock
	private Map<String, Lunched> lunchedMap;

	@InjectMocks
	private LocalLunchService service;

	@Test
	void testCache(@Mock LunchProcess process) {
		var item = new LunchItem("test-cmd", "test", "cmd", List.of());
		var lunched = new Lunched(process, item);

		given(supplier.get())
			.willReturn(lunched);
		given(luncher.launch(item))
			.willReturn(Flux.from(Mono.fromSupplier(supplier)));
		given(process.getPid())
			.willReturn("test-pid");
		given(process.exitCode())
			.willReturn(44);

		var result = service.launch(item);

		assertThat(List.of(result.blockFirst(), result.blockLast()))
			.extracting(LunchInfo::getPid, LunchInfo::getExitCode)
			.containsOnly(tuple("test-pid", 44))
			;

		then(supplier)
			.should().get();
	}
}
