package org.bool.lunch;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
    public static void main(String[] args) throws InterruptedException {
    	ExecutorService pool = Executors.newFixedThreadPool(classesToRun.length);
    	
    	for (int i = 0; i < classesToRun.length; ++i) {
    		Luncher luncher = Luncher.create(classesToRun[i], arguments[i]);
    		pool.execute(luncher);
    		log.info("Started {} with arguments: {}", classesToRun[i], Arrays.asList(arguments[i]));
    	}
    	
    	pool.shutdown();
    }
}