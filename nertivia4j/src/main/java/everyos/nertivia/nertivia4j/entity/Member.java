package everyos.nertivia.nertivia4j.entity;

import everyos.nertivia.nertivia4j.NertiviaClient;
import everyos.nertivia.nertivia4j.NertiviaInstance;
import reactor.core.publisher.Mono;

public class Member extends User {
	public Member(NertiviaClient client, NertiviaInstance instance, long userID, long serverID, boolean isBot) {
		super(client, instance, userID, isBot);
	}

	public Mono<Void> ban(String reason) {
		return Mono.empty();
	}

	public Mono<Void> kick(String reason) {
		return Mono.empty();
	}
}
