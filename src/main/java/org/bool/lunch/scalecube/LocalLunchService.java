package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.Lunched;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;

public class LocalLunchService implements LunchService {

    private final LunchRunner lunchRunner;
    
    private final Executor executor;
    
    private final Map<String, Lunched> lunchedMap;
    
    public LocalLunchService(LunchRunner lunchRunner, Executor executor) {
        this(lunchRunner, executor, new ConcurrentHashMap<>());
    }

    public LocalLunchService(LunchRunner lunchRunner, Executor executor, Map<String, Lunched> lunchedMap) {
        this.lunchRunner = lunchRunner;
        this.executor = executor;
        this.lunchedMap = lunchedMap;
    }

    @Override
    public Flux<Lunched> launch(LunchItem item) {
        ReplayProcessor<Lunched> result = ReplayProcessor.cacheLast();
        result.subscribe(lunch -> lunchedMap.put(lunch.getPid(), lunch));
        executor.execute(() -> launch(item, result.sink()));
        return result;
    }
    
    private void launch(LunchItem item, FluxSink<Lunched> sink) {
        try {
            Lunched lunch = lunchRunner.launch(item);
            
            lunchedMap.put(lunch.getPid(), lunch);
            
            sink.next(new Lunched(lunch.getPid(), null, lunch.getLunchItem()));
            
            int exitCode = lunch.getProcess().waitFor();
            if (exitCode != 0) {
                throw new ProcessTerminatedException(lunch.getPid(), exitCode);
            }
            sink.complete();
        } catch (Exception e) {
            sink.error(e);
        }
    }
    
    @Override
    public Mono<Void> land(String pid) {
        return Mono.just(pid)
                .map(lunchedMap::get)
                .filter(Objects::nonNull)
                .map(Lunched::getProcess)
                .doOnNext(Process::destroy)
                .single()
                .then();
    }

    @Override
    public Mono<List<Stat>> stats() {
        return Mono.just(lunchedMap.values())
                .map(values -> values.stream().map(this::buildStat).collect(Collectors.toList()))
                .cache();
    }
    
    private Stat buildStat(Lunched lunched) {
        return new Stat(lunched.getPid(), lunched.getLunchItem(), lunched.getProcess().isAlive() ? null : lunched.getProcess().exitValue());
    }
}