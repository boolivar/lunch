package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.Lunched;

import io.scalecube.services.annotations.Service;
import io.scalecube.services.annotations.ServiceMethod;
import reactor.core.publisher.Flux;

@Service
public interface LunchService {
    @ServiceMethod
    Flux<Lunched> launch(LunchItem item);
}