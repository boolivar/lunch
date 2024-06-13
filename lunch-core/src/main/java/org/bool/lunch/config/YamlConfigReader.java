package org.bool.lunch.config;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlConfig.WriteClassName;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class YamlConfigReader {

	private YamlConfig config;

	public YamlConfigReader() {
		this(defaultYamlConfig());
	}

	public <T> Mono<T> read(Callable<Reader> source, Class<T> type) {
		return Mono.fromCallable(() -> {
			try (var reader = source.call()) {
				return new YamlReader(reader, config).read(type);
			}
		});
	}

	public Mono<String> toYamlString(Object value) {
		return Mono.fromCallable(() -> {
			var writer = new StringWriter();
			var yamlWriter = new YamlWriter(writer, config);
			yamlWriter.write(value);
			yamlWriter.close();
			return writer.toString();
		});
	}

	private static YamlConfig defaultYamlConfig() {
		var config = new YamlConfig();
		config.setAllowDuplicates(false);
		config.setScalarSerializer(File.class, new FileSerializer());
		config.writeConfig.setKeepBeanPropertyOrder(true);
		config.writeConfig.setWriteClassname(WriteClassName.NEVER);
		return config;
	}
}
