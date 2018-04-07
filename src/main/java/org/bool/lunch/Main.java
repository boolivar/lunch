package org.bool.lunch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlReader;

public class Main {

	private static final String CONFIG_YML = "/config.yml";
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
    public static void main(String[] args) throws InterruptedException, IOException, ReflectiveOperationException {
    	Lunch lunch = loadLunch(args.length > 0 ? args[0] : null);
    	log.info("Lunch loaded: {}", lunch);
    	run(lunch);
    	log.info("Lunch started");
    }
    
    private static void run(Lunch lunch) throws ClassNotFoundException {
    	CachedRunnerFactory<String> factory = new CachedRunnerFactory<>(new DefaultRunnerFactory(), RunnerType::valueOf);
    	LunchPad lunchPad = new LunchPad(factory::lookup);
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