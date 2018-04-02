package org.bool.lunch;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlWriter;

import kafka.Kafka;

public class Main {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	private static final Class<?>[] classesToRun = {
		QuorumPeerMain.class,
		Kafka.class
	};
	
	private static final String[][] arguments = {
		{"C:\\bin\\kafka_2.11-1.0.0\\config\\zookeeper.properties"},
		{"C:\\bin\\kafka_2.11-1.0.0\\config\\server.properties"},
	};
	
    public static void main(String[] args) throws InterruptedException, IOException, ReflectiveOperationException {
    	Lunch lunch = lunch();
    	
    	ExecutorService pool = Executors.newFixedThreadPool(lunch.getItems().size());
    	
    	for (LunchItem item : lunch.getItems()) {
    		Luncher luncher = Luncher.create(item.getClassName(), item.getArgs());
    		pool.execute(luncher);
    		log.info("Started {} with arguments: {}", item.getClassName(), item.getArgs());
    	}
    	
    	pool.shutdown();
    }
    
    private static void writeConfiguration(String fileName, Object object) throws IOException {
    	YamlWriter yamlWriter = new YamlWriter(new BufferedWriter(new FileWriter(fileName)));
    	try {
    		yamlWriter.write(object);
    		log.info("Write configuration {} to file {}", object, fileName);
    	} finally {
    		yamlWriter.close();
    	}
    }
    
    private static Lunch lunch() {
    	Lunch lunch = new Lunch();
    	List<LunchItem> items = new ArrayList<>(classesToRun.length);
    	for (int i = 0; i < classesToRun.length; ++i) {
    		LunchItem lunchItem = new LunchItem();
    		lunchItem.setClassName(classesToRun[i].getName());
    		lunchItem.setArgs(Arrays.asList(arguments[i]));
    		items.add(lunchItem);
    	}
    	lunch.setItems(items);
    	return lunch;
    }
}