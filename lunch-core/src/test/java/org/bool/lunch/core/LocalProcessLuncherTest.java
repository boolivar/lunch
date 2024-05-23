package org.bool.lunch.core;

import org.bool.lunch.LunchItem;
import org.bool.lunch.api.LunchedItem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class LocalProcessLuncherTest {

	@Mock
	private ProcessBuilderMapper mapper;

	@InjectMocks
	private LocalProcessLuncher luncher;

	@Test
	void test(@Mock ProcessBuilder builder, @Mock Process process) throws IOException {
		var item = LunchItem.builder().name("Test").build();

		given(mapper.map(item))
			.willReturn(builder);
		given(builder.start())
			.willReturn(process);

		var mono = luncher.launch(item);
		verifyNoInteractions(builder, process);

		StepVerifier.create(mono)
			.assertNext(value -> assertThat(value).returns("Test", LunchedItem::getName).hasFieldOrPropertyWithValue("process", process))
			.verifyComplete();
	}
}
