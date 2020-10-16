package everyos.bot.luwu.discord;

import everyos.bot.luwu.core.entity.ClientWrapper;
import everyos.discord.chat4d.DiscordChatClient;

public class DiscordClientBuilder {
	private String token;

	public void setToken(String token) {
		this.token = token;
	}
	
	public ClientWrapper build(int i, String string) {
		return new ClientWrapper(new DiscordChatClient(token), new DiscordClientBehaviour());
	}
}
