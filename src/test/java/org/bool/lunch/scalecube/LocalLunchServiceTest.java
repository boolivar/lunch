package org.bool.lunch.scalecube;

import org.apache.commons.lang3.mutable.MutableObject;
import org.bool.lunch.LunchItem;
import org.bool.lunch.Lunched;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.function.Supplier;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class LocalLunchServiceTest {

	@Mock
	Supplier<Lunched> supplier;
	
	@Mock
	Luncher luncher;
	
	@Mock
	Map<String, Lunched> map;
	
	@InjectMocks
	LocalLunchService service;
	
	@Test
	void testCache() throws InterruptedException {
		Lunched lunched = new Lunched("test-pid", null, null);
		
		given(supplier.get())
			.willReturn(lunched);
		given(luncher.launch(any()))
			.willReturn(Flux.from(Mono.fromSupplier(supplier)));
		
		MutableObject<Lunched> subscriber = new MutableObject<>();
		
		Flux<Lunched> result = service.launch(new LunchItem());
		result.subscribe(subscriber::setValue);
		
		then(supplier)
			.should().get();
		
		assertSame(lunched, result.blockFirst());
		assertSame(lunched, subscriber.getValue());
	}
}
