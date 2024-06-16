package org.bool.lunch.config;

import org.bool.lunch.LunchItem;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YamlObjectFactoryTest {

	private static final String TEST_CONTENT = readResource("/test");

	@TempDir
	private Path tempDir;

	@Captor
	private ArgumentCaptor<Callable<Reader>> captor;

	private final YamlConfigReader yamlReader = mock(YamlConfigReader.class);

	private final YamlObjectFactory<LunchItem> factory = new YamlObjectFactory<>(LunchItem.class, yamlReader);

	private final LunchItem lunchExample = new LunchItem();

	@SneakyThrows
	private static String readResource(String resource) {
		return IOUtils.resourceToString(resource, StandardCharsets.UTF_8);
	}

	@BeforeEach
	void setupYamlReader() {
		when(yamlReader.read(any(), same(LunchItem.class)))
			.thenReturn(Mono.just(lunchExample));
	}

	void verifyYamlReaderCall() throws Exception {
		verify(yamlReader).read(captor.capture(), same(LunchItem.class));
		try (Reader reader = captor.getValue().call()) {
			assertThat(((BufferedReader) reader).readLine())
				.isEqualTo(TEST_CONTENT);
		}
	}

	@Test
	void testFile() throws Exception {
		var file = tempDir.resolve("test-file");
		Files.writeString(file, TEST_CONTENT);
		assertThat(factory.readFile(file).block())
			.isSameAs(lunchExample);
		verifyYamlReaderCall();
	}

	@Test
	void testResource() throws Exception {
		assertThat(factory.readResource("/test").block())
			.isSameAs(lunchExample);
		verifyYamlReaderCall();
	}

	@Test
	void testString() throws Exception {
		assertThat(factory.readString(TEST_CONTENT).block())
			.isSameAs(lunchExample);
		verify(yamlReader).read(captor.capture(), same(LunchItem.class));
		try (Reader reader = captor.getValue().call()) {
			assertThat(reader)
				.isInstanceOf(StringReader.class);
			assertThat(IOUtils.buffer(reader).readLine())
				.isEqualTo(TEST_CONTENT);
		}
	}

	@Test
	void testInputStream() throws Exception {
		assertThat(factory.readStream(() -> IOUtils.toInputStream(TEST_CONTENT, StandardCharsets.UTF_8)).block())
			.isSameAs(lunchExample);
		verifyYamlReaderCall();
	}
}
