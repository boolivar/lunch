package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.Lunched;

import java.util.List;

import io.scalecube.services.annotations.Service;
import io.scalecube.services.annotations.ServiceMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface LunchService {
    
    @ServiceMethod
    Flux<Lunched> launch(LunchItem item);
    
    @ServiceMethod
    Mono<Void> land(String pid);
    
    @ServiceMethod
    Mono<List<Stat>> stats();
}