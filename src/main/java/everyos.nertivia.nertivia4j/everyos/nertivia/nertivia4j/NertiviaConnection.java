package everyos.nertivia.nertivia4j;

import everyos.nertivia.nertivia4j.event.Event;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NertiviaConnection {
	public Mono<Void> logout();
	public <T extends Event> Flux<T> listen(Class<T> e);
}
