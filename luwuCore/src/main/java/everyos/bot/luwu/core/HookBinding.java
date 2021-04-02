package everyos.bot.luwu.core;

import java.util.function.Function;

import everyos.bot.luwu.core.entity.event.Event;
import reactor.core.publisher.Mono;

class HookBinding<T extends Event> {
	private boolean synchronous;
	private Class<T> eventClass;
	private Function<T, Mono<Void>> func;

	protected HookBinding(boolean synchronous, Class<T> event, Function<T, Mono<Void>> func) {
		this.synchronous = synchronous;
		this.eventClass = event;
		this.func = func;
	}
	
	public Mono<Void> apply(Mono<Void> chain, Event e) {
		if (eventClass.isAssignableFrom(e.getClass())) {
			@SuppressWarnings("unchecked")
			Mono<Void> resultingMono =
				Mono.fromCallable(()->{
					return func.apply((T) e)
						.onErrorResume(ex->{
							ex.printStackTrace();
							return Mono.empty();
						});
				})
				.flatMap(m->m);
			
			if (synchronous) return chain.then(resultingMono);
			return chain.and(resultingMono);
		}
		
		return chain;
	}
}
