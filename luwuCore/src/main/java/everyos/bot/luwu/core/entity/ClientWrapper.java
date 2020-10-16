package everyos.bot.luwu.core.entity;

import everyos.bot.chat4j.ChatClient;
import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.client.ClientBehaviour;

public class ClientWrapper {
	private ChatClient client;
	private ClientBehaviour behaviour;

	public ClientWrapper(ChatClient client, ClientBehaviour behaviour) {
		this.client = client;
		this.behaviour = behaviour;
	}
	
	public Client create(BotEngine engine) {
		return new Client(engine, client, behaviour);
	}
}
