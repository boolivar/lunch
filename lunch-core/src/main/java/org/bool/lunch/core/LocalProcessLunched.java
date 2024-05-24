package org.bool.lunch.core;

import org.bool.lunch.api.LunchedItem;

import lombok.AllArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Mono;

import java.lang.ProcessHandle.Info;

@ToString
@AllArgsConstructor
public class LocalProcessLunched implements LunchedItem {

	private final String name;

	private final Process process;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPid() {
		return String.valueOf(process.pid());
	}

	@Override
	public Info getInfo() {
		return process.info();
	}

	@Override
	public boolean isAlive() {
		return process.isAlive();
	}

	@Override
	public Mono<Integer> exitCode() {
		return Mono.fromFuture(process.onExit())
			.map(Process::exitValue);
	}

	@Override
	public Mono<Void> terminate(boolean force) {
		return Mono.fromRunnable(force ? process::destroyForcibly : process::destroy);
	}
}
