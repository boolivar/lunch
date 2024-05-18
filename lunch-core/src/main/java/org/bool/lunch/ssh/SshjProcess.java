package org.bool.lunch.ssh;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.bool.lunch.LunchProcess;

import net.schmizz.sshj.connection.channel.direct.Session.Command;

public class SshjProcess implements LunchProcess {

	private final String pid;

	private final Command cmd;

	public SshjProcess(Command cmd) {
		this(cmd.getID() + ":" + cmd.getRecipient(), cmd);
	}

	public SshjProcess(String pid, Command cmd) {
		this.pid = pid;
		this.cmd = cmd;
	}

	@Override
	public String getPid() {
		return pid;
	}

	@Override
	public Integer waitFor(Duration duration) throws InterruptedException {
		try {
			if (duration == INFINITE_WAIT) {
				cmd.join();
			} else if (!duration.isZero()) {
				cmd.join(duration.toMillis(), TimeUnit.MILLISECONDS);
			}
			return cmd.getExitStatus();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void destroy() {
		try {
			cmd.close();
		} catch (IOException e) {
			throw new RuntimeException("Error close command channel #" + pid + ": " + cmd, e);
		}
	}
}
