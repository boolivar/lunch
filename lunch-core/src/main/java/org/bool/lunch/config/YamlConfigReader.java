package org.bool.lunch.config;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlReader;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.io.Reader;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class YamlConfigReader {

	private YamlConfig config;

	public YamlConfigReader() {
		this(new YamlConfig());
	}

	public <T> Mono<T> read(Callable<Reader> source, Class<T> type) {
		return Mono.fromCallable(() -> {
			try (var reader = source.call()) {
				return new YamlReader(reader, config).read(type);
			}
		});
	}
}
