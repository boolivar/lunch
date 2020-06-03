package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.Lunched;

import java.util.concurrent.Executor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;

public class LocalLunchService implements LunchService {

    private final LunchRunner lunchRunner;
    
    private final Executor executor;

    public LocalLunchService(LunchRunner lunchRunner, Executor executor) {
        this.lunchRunner = lunchRunner;
        this.executor = executor;
    }

    @Override
    public Flux<Lunched> launch(LunchItem item) {
        ReplayProcessor<Lunched> result = ReplayProcessor.cacheLast();
        executor.execute(() -> launch(item, result.sink()));
        return result;
    }
    
    private void launch(LunchItem item, FluxSink<Lunched> sink) {
        try {
            Lunched lunch = lunchRunner.launch(item);
            sink.next(lunch);
            
            int exitCode = lunch.getProcess().waitFor();
            if (exitCode != 0) {
                throw new ProcessTerminatedException(lunch.getPid(), exitCode);
            }
            sink.complete();
        } catch (Exception e) {
            sink.error(e);
        }
    }
}