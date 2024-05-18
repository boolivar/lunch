package org.bool.lunch.ssh;

import net.schmizz.sshj.Config;
import net.schmizz.sshj.DefaultConfig;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

import org.apache.commons.lang3.StringUtils;
import org.bool.lunch.LunchProcess;
import org.bool.lunch.Runner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SshjRunner implements Runner {

	private static final Logger log = LoggerFactory.getLogger(SshjRunner.class);

	private final Config sshjConfig;

	private final ExecutorService executor;

	public SshjRunner() {
		this(new DefaultConfig(), Executors.newCachedThreadPool());
	}

	public SshjRunner(Config sshjConfig, ExecutorService executor) {
		this.sshjConfig = sshjConfig;
		this.executor = executor;
	}

	@Override
	public LunchProcess run(String host, Collection<String> args) {
		try {
			CompletableFuture<Command> result = new CompletableFuture<>();
			executor.submit(() -> startSshjProcess(host, args, result));
			return new SshjProcess(result.get());
		} catch (Exception e) {
			throw new RuntimeException("Error start command " + args, e);
		}
	}
	
	private void startSshjProcess(String host, Collection<String> args, CompletableFuture<Command> result) { 
		try (SSHClient client = new SSHClient(sshjConfig)) {
			client.connect(host);
			try (Session session = client.startSession(); Command cmd = session.exec(StringUtils.join(args, ' '))) {
				result.complete(cmd);
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream(), StandardCharsets.UTF_8))) {
					String line;
					while ((line = reader.readLine()) != null) {
						log.info("SSH process {}/{}: {}", cmd.getID(), cmd.getRecipient(), line);
					}
					log.info("SSH process {}/{} complete", cmd.getID(), cmd.getRecipient());
				} catch (Exception e) {
					log.error("SSH process {}/{} error", cmd.getID(), cmd.getRecipient(), e);
				}
			}
		} catch (Throwable e) {
			result.completeExceptionally(e);
		}
	}
}
