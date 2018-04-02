package org.bool.lunch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import kafka.Kafka;

public class Main {
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	
	private static final Class<?>[] classesToRun = {
		QuorumPeerMain.class,
//		Kafka.class
	};
	
    public static void main(String[] args) throws InterruptedException {
    	ExecutorService pool = Executors.newFixedThreadPool(classesToRun.length);
    	for (Class<?> cls : classesToRun) {
    		Luncher luncher = Luncher.create(cls);
    		pool.execute(luncher);
    		log.info("Started " + cls);
    	}
    	pool.shutdown();
    }
}