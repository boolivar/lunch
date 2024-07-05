package org.bool.lunch.akka;

import org.bool.lunch.Lunch;
import org.bool.lunch.config.YamlObjectFactory;

import lombok.SneakyThrows;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Option;
import reactor.core.publisher.Mono;

import java.nio.file.Path;

@CommandLine.Command(name = "AkkaLunchCli", mixinStandardHelpOptions = true)
public class AkkaLunchCli implements Runnable {

	static class Config {

		private final YamlObjectFactory<Lunch> factory = new YamlObjectFactory<>(Lunch.class);

		@Option(names = "--file", description = "yaml config file")
		private Path file;

		@Option(names = "--yaml", description = "yaml config")
		private String yaml;

		@SneakyThrows
		public Mono<Lunch> load() {
			return file != null ? factory.readFile(file) : factory.readString(yaml);
		}
	}

	@Option(names = "--address", description = "bootstrap address")
	private String address;

	@ArgGroup
	private Config config;

	@Override
	public void run() {
		Mono.justOrEmpty(config)
			.flatMap(Config::load)
			.switchIfEmpty(Mono.fromSupplier(Lunch::new))
			.map(lunch -> address != null ? new AkkaLunch(address, lunch) : new AkkaLunch(lunch))
			.flatMap(AkkaLunch::run)
			.block();
	}

	public static void main(String[] args) {
		new CommandLine(new AkkaLunchCli()).execute(args);
	}
}
