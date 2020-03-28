package org.bool.lunch;

import com.esotericsoftware.yamlbeans.YamlReader;

import org.bool.lunch.akka.AkkaPad;
import org.bool.lunch.akka.LunchItemActorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {

	private static final String CONFIG_YML = "/config.yml";
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) throws InterruptedException, IOException, ReflectiveOperationException {
		Lunch lunch = loadLunch(args.length > 0 ? args[0] : null);
		log.info("Lunch loaded: {}", lunch);
		run(lunch);
		log.info("Lunch finished");
	}
	
	private static void run(Lunch lunch) throws ClassNotFoundException {
		CachedRunnerFactory<String> factory = new CachedRunnerFactory<>(new DefaultRunnerFactory(), RunnerType::valueOf);
		LunchRunner runner = new LunchRunner(factory::lookup, PidReader.DEFAULT);
		LunchBox lunchBox = new LunchBox(runner);
		LaunchPad lunchPad = new AkkaPad(new LunchItemActorFactory(lunchBox));
		lunchPad.launch(lunch);
	}
	
	private static Lunch loadLunch(String fileName) throws IOException {
		log.info("Read lunch from file: {}", fileName);
		try (InputStream in = openStream(fileName)) {
			YamlReader reader = new YamlReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			return reader.read(Lunch.class);
		}
	}
	
	private static InputStream openStream(String fileName) throws IOException {
		if (fileName != null) {
			return new FileInputStream(fileName);
		}
		return Main.class.getResourceAsStream(CONFIG_YML);
	}
}