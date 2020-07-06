package org.bool.lunch.scalecube;

import org.apache.commons.lang3.mutable.MutableObject;
import org.bool.lunch.LunchItem;
import org.bool.lunch.LunchProcess;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.Lunched;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.*;

class LuncherTest {
	
	LunchRunner lunchRunner = mock(LunchRunner.class);
	
	LunchProcess process = mock(LunchProcess.class);
	
	Luncher luncher = new Luncher(lunchRunner, Schedulers.immediate());

	@Test
	void testLaunch() {
		luncher.launch(new LunchItem());

		then(lunchRunner)
			.shouldHaveNoInteractions();
	}

	@Test
	void testComplete() {
		LunchItem lunchItem = new LunchItem("test");

		given(lunchRunner.launch(lunchItem))
			.willReturn(new Lunched(process, lunchItem));
		
		Lunched lunched = luncher.launch(lunchItem)
			.blockLast();
		
		assertSame(process, lunched.getProcess());
	}

	@Test
	void testError() throws InterruptedException {
		LunchItem lunchItem = new LunchItem("test");
		
		given(process.waitFor())
			.willReturn(10);
		given(lunchRunner.launch(lunchItem))
			.willReturn(new Lunched(process, lunchItem));

		MutableObject<Lunched> lunchedResult = new MutableObject<>();
		MutableObject<Throwable> lunchedError = new MutableObject<>();
		
		luncher.launch(lunchItem)
			.subscribe(lunchedResult::setValue, lunchedError::setValue);
		
		assertSame(process, lunchedResult.getValue().getProcess());
		assertEquals(10, ((ProcessTerminatedException) lunchedError.getValue()).getExitCode());
		assertEquals("pid-test", ((ProcessTerminatedException) lunchedError.getValue()).getPid());
	}
	
	@Test
	void testColdPublish() {
		LunchItem lunchItem = new LunchItem();
		given(lunchRunner.launch(any()))
			.willReturn(new Lunched(process, null));
		
		Flux<Lunched> flux = luncher.launch(lunchItem);
		
		assertEquals(1, flux.count().block());
		assertEquals(1, flux.count().block());
		
		then(lunchRunner)
			.should(times(2)).launch(lunchItem);
	}
}