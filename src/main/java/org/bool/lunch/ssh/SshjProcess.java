package org.bool.lunch.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

import org.bool.lunch.LunchProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class SshjProcess implements LunchProcess {

	private static final Logger log = LoggerFactory.getLogger(SshjProcess.class);

	private final String pid;

	private final SSHClient client;

	private final Command cmd;

	public SshjProcess(String pid, SSHClient client, Command cmd) {
		this.pid = pid;
		this.client = client;
		this.cmd = cmd;
	}

	@Override
	public String getPid() {
		return pid;
	}

	@Override
	public Integer waitFor(Duration duration) throws InterruptedException {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(cmd.getInputStream(), StandardCharsets.UTF_8));

			String line = reader.readLine();
			while (line != null) {
				log.info("Ssh #{}: {}", pid, line);
				line = reader.readLine();
			}

			if (duration == null) {
				cmd.join();
			} else {
				cmd.join(duration.toMillis(), TimeUnit.MILLISECONDS);
			}

			return cmd.getExitStatus();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				throw new RuntimeException("Error close client #" + pid, e);
			}
		}
	}

	@Override
	public void destroy() {
		try {
			client.close();
		} catch (IOException e) {
			throw new RuntimeException("Error close channel #" + pid, e);
		}
	}
}
