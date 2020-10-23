package everyos.bot.luwu.core.entity;

import java.util.function.Function;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.client.ClientBehaviour;
import reactor.core.publisher.Mono;

public class Client {
	private BotEngine bot;
	private ChatClient client;
	private ClientBehaviour behaviour;
	private int id;

	public Client(BotEngine bot, ChatClient client, ClientBehaviour behaviour) {
		this.bot = bot;
		this.client = client;
		this.behaviour = behaviour;
		this.id = id;
	}

	public Mono<Member> getSelfAsUser() {
		return Mono.empty();
	}

	public BotEngine getBotEngine() {
		return bot;
	}

	public ClientBehaviour getBehaviour() {
		return this.behaviour;
	}

	public Mono<Void> login(Function<Connection, Mono<Void>> func) {
		return client.login(connection->{
			return func.apply(new Connection(this, connection));
		});
	}

	public int getID() {
		return this.id;
	}
}
