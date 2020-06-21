package org.bool.lunch.scalecube;

import org.apache.commons.lang3.mutable.MutableObject;
import org.bool.lunch.LunchItem;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.Lunched;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

class LuncherTest {
	
	LunchRunner lunchRunner = mock(LunchRunner.class);
	
	Process process = mock(Process.class);
	
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
			.willReturn(new Lunched("test-pid", process, lunchItem));
		
		Lunched lunched = luncher.launch(lunchItem)
			.blockLast();
		
		assertEquals("test-pid", lunched.getPid());
	}

	@Test
	void testError() throws InterruptedException  {
		LunchItem lunchItem = new LunchItem("test");
		
		given(process.waitFor())
			.willReturn(10);
		given(lunchRunner.launch(lunchItem))
			.willReturn(new Lunched("pid-test", process, lunchItem));

		MutableObject<Lunched> lunchedResult = new MutableObject<>();
		MutableObject<Throwable> lunchedError = new MutableObject<>();
		
		luncher.launch(lunchItem)
			.subscribe(lunchedResult::setValue, lunchedError::setValue);
		
		assertEquals("pid-test", lunchedResult.getValue().getPid());
		assertEquals(10, ((ProcessTerminatedException) lunchedError.getValue()).getExitCode());
		assertEquals("pid-test", ((ProcessTerminatedException) lunchedError.getValue()).getPid());
	}
	
	@Test
	void testColdPublish() {
		LunchItem lunchItem = new LunchItem();
		given(lunchRunner.launch(any()))
			.willReturn(new Lunched("pid-test", process, null));
		
		Flux<Lunched> flux = luncher.launch(lunchItem);
		
		assertEquals(1, flux.count().block());
		assertEquals(1, flux.count().block());
		
		then(lunchRunner)
			.should(times(2)).launch(lunchItem);
	}
}