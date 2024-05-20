package org.bool.lunch.scalecube;

import org.apache.commons.lang3.mutable.MutableObject;
import org.assertj.core.api.InstanceOfAssertFactories;

import org.bool.lunch.LunchItem;
import org.bool.lunch.LunchProcess;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.Lunched;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

class LuncherTest {

	private final LunchRunner lunchRunner = mock(LunchRunner.class);

	private final LunchProcess process = mock(LunchProcess.class);

	private final Luncher luncher = new Luncher(lunchRunner, Schedulers.immediate());

	@Test
	void testLaunch() {
		luncher.launch(new LunchItem());

		then(lunchRunner)
			.shouldHaveNoInteractions();
	}

	@Test
	void testComplete() {
		LunchItem lunchItem = new LunchItem();

		given(lunchRunner.launch(lunchItem))
			.willReturn(new Lunched(process, lunchItem));
		
		Lunched lunched = luncher.launch(lunchItem)
			.blockLast();

		assertThat(lunched)
			.returns(process, Lunched::getProcess);
	}

	@Test
	void testError() throws InterruptedException {
		LunchItem lunchItem = new LunchItem();
		Lunched lunched = new Lunched(process, lunchItem);

		given(process.waitFor())
			.willReturn(10);
		given(process.getPid())
			.willReturn("pid-test");
		given(lunchRunner.launch(lunchItem))
			.willReturn(lunched);

		MutableObject<Lunched> lunchedResult = new MutableObject<>();
		MutableObject<Throwable> lunchedError = new MutableObject<>();

		luncher.launch(lunchItem)
			.subscribe(lunchedResult::setValue, lunchedError::setValue);

		assertThat(lunchedResult.getValue())
			.isSameAs(lunched);
		assertThat(lunchedError.getValue())
			.asInstanceOf(InstanceOfAssertFactories.type(ProcessTerminatedException.class))
			.returns(10, ProcessTerminatedException::getExitCode)
			.returns("pid-test", ProcessTerminatedException::getPid)
			;
	}

	@Test
	void testColdPublish() {
		LunchItem lunchItem = new LunchItem();
		given(lunchRunner.launch(lunchItem))
			.willReturn(new Lunched(process, null));

		Flux<Lunched> flux = luncher.launch(lunchItem);
		assertThat(flux.count().block())
			.isEqualTo(1);
		assertThat(flux.count().block())
			.isEqualTo(1);

		then(lunchRunner)
			.should(times(2)).launch(lunchItem);
	}
}