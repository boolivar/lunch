package org.bool.lunch.config;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apache.commons.io.IOUtils;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class YamlObjectFactory<T> {

	private final Class<T> type;

	private final YamlConfigReader yamlReader;

	@NonNull
	private final Charset charset;

	public YamlObjectFactory(Class<T> type) {
		this(type, new YamlConfigReader());
	}

	public YamlObjectFactory(Class<T> type, YamlConfigReader yamlReader) {
		this(type, yamlReader, StandardCharsets.UTF_8);
	}

	public Mono<T> readFile(Path file) {
		return yamlReader.read(() -> Files.newBufferedReader(file, charset), type);
	}

	public Mono<T> readResource(String resource) {
		return yamlReader.read(() -> bufferedReader(getClass().getResourceAsStream(resource)), type);
	}

	public Mono<T> readStream(Callable<InputStream> inputStream) {
		return yamlReader.read(() -> bufferedReader(inputStream.call()), type);
	}

	public Mono<T> read(Callable<Reader> reader) {
		return yamlReader.read(() -> IOUtils.buffer(reader.call()), type);
	}

	private BufferedReader bufferedReader(InputStream in) {
		return new BufferedReader(new InputStreamReader(in, charset));
	}
}
