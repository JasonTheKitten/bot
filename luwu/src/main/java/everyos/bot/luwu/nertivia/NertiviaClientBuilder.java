package everyos.bot.luwu.nertivia;

import everyos.bot.luwu.core.entity.ClientWrapper;
import everyos.nertivia.chat4n.NertiviaChatClient;

public class NertiviaClientBuilder {
	private String token;

	public void setToken(String token) {
		this.token = token;
	}
	
	public ClientWrapper build(int id, String string) {
		return new ClientWrapper(new NertiviaChatClient(token), new NertiviaClientBehaviour(), id);
	}
}
