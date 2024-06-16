package org.bool.lunch.config;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
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
		return read(() -> Files.newBufferedReader(file, charset));
	}

	public Mono<T> readResource(String resource) {
		return read(() -> bufferedReader(getClass().getResourceAsStream(resource)));
	}

	public Mono<T> readStream(Callable<InputStream> inputStream) {
		return read(() -> bufferedReader(inputStream.call()));
	}

	public Mono<T> readString(String yaml) {
		return read(() -> new StringReader(yaml));
	}

	private Mono<T> read(Callable<Reader> reader) {
		return yamlReader.read(reader, type);
	}

	private BufferedReader bufferedReader(InputStream in) {
		return new BufferedReader(new InputStreamReader(in, charset));
	}
}
