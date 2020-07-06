package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.Lunched;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;

public class Luncher {
	
	private final LunchRunner runner;
	
	private final Scheduler scheduler;
	
	public Luncher(LunchRunner runner, Scheduler scheduler) {
		this.runner = runner;
		this.scheduler = scheduler;
	}
	
	public Flux<Lunched> launch(LunchItem item) {
		return Flux.<Lunched>create(sink -> launch(item, sink))
				.subscribeOn(scheduler)
				;
	}
	
	private void launch(LunchItem item, FluxSink<Lunched> sink) {
		try {
			Lunched lunch = runner.launch(item);

			sink.next(lunch);

			int exitCode = lunch.getProcess().waitFor();
			if (exitCode != 0) {
				throw new ProcessTerminatedException(lunch.getProcess().getId(), exitCode);
			}
			sink.complete();
		} catch (Exception e) {
			sink.error(e);
		}
	}
}