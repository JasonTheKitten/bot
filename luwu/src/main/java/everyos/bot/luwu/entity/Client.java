package everyos.bot.luwu.entity;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.luwu.BotInstance;
import reactor.core.publisher.Mono;

public class Client {
	private BotInstance bot;

	public Client(BotInstance bot, ChatClient client) {
		this.bot = bot;
	}

	public Mono<Member> getSelfAsUser() {
		return Mono.empty();
	}

	public BotInstance getBotInstance() {
		return bot;
	}
}
