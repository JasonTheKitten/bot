package everyos.bot.luwu.core;

import java.time.Duration;
import java.util.function.Function;

import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class TimedTask {
	private long period;
	private Function<Connection, Mono<Void>> func;

	protected TimedTask(long period, Function<Connection, Mono<Void>> func) {
		this.period = period;
		this.func = func;
	}
	
	public Mono<Void> apply(Connection c, Mono<Void> chain) {
		Mono<Void> resultingMono =
			Mono.fromCallable(()->{
				return func.apply(c)
					.onErrorResume(ex->{
						ex.printStackTrace();
						return Mono.empty();
					});
			})
			.flatMap(m->m);

		Mono<Void> repeating =
			Flux.interval(Duration.ofMillis(0), Duration.ofMillis(period))
			.flatMap(i->resultingMono)
			.then();
		
		return chain.and(repeating);
	}
}
