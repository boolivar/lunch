package org.bool.lunch.akka;

import org.bool.lunch.Lunch;
import org.bool.lunch.core.LocalProcessLuncher;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	private static final String CONFIG_YML = "config.yml";

	private static final Logger log = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws InterruptedException, IOException, ReflectiveOperationException {
		Lunch lunch = loadLunch(args.length > 0 ? args[0] : null);
		log.info("Lunch loaded: {}", lunch);
		run(lunch);
		log.info("Lunch finished");
	}
	
	private static void run(Lunch lunch) throws ClassNotFoundException {
		var luncher = new LocalProcessLuncher();
		AkkaPad lunchPad = new AkkaPad(new LunchItemActorFactory(luncher));
		lunchPad.launch(lunch);
	}
	
	private static Lunch loadLunch(String fileName) throws IOException {
		Path configPath = Paths.get(fileName != null ? fileName : CONFIG_YML);
		log.info("Read lunch from file: {}", configPath);
		try (Reader reader = Files.newBufferedReader(configPath)) {
			return new YamlReader(reader).read(Lunch.class);
		}
	}
}