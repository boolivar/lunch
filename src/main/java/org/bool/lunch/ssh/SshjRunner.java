package org.bool.lunch.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

import org.apache.commons.lang3.StringUtils;
import org.bool.lunch.LunchProcess;
import org.bool.lunch.Runner;

import java.util.Collection;
import java.util.function.Function;

public class SshjRunner implements Runner {

	private final Function<String, SSHClient> clientFactory;

	public SshjRunner(Function<String, SSHClient> clientFactory) {
		this.clientFactory = clientFactory;
	}

	@Override
	public LunchProcess run(String host, Collection<String> args) {
		SSHClient client = clientFactory.apply(host);
		try {
			Command cmd = client.startSession().exec(StringUtils.join(args, ' '));
			return new SshjProcess(String.valueOf(cmd.getID()), client, cmd);
		} catch (Exception e) {
			RuntimeException re = new RuntimeException("Error " + host + " command " + args, e);
			try {
				client.close();
			} catch (Exception se) {
				re.addSuppressed(se);
			}
			throw re;
		}
	}
}
