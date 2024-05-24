package org.bool.lunch.akka;

import org.bool.lunch.api.LunchedItem;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class LunchItemActor extends AbstractBehavior<Command> {

	private final LunchedItem lunched;
	
	public static class Stopped implements Command {
		
		private final int exitCode;

		public int getExitCode() {
			return exitCode;
		}

		public Stopped(int exitCode) {
			this.exitCode = exitCode;
		}
	}
	
	public LunchItemActor(ActorContext<Command> context, LunchedItem lunched) {
		super(context);
		this.lunched = lunched;
	}

	@Override
	public Receive<Command> createReceive() {
		return newReceiveBuilder()
				.onMessage(Stopped.class, this::onStop)
				.onSignal(PostStop.class, this::onPostStop)
				.build();
	}
	
	private Behavior<Command> onStop(Stopped ignored) {
		return Behaviors.stopped();
	}
	
	private Behavior<Command> onPostStop(PostStop ignored) {
		if (lunched.isAlive()) {
			lunched.terminate(false);
		}
		return this;
	}
}
