package everyos.bot.luwu.command;

import everyos.bot.luwu.BotInstance;
import everyos.bot.luwu.client.ClientBehaviour;
import everyos.bot.luwu.entity.Channel;
import everyos.bot.luwu.entity.Client;
import everyos.bot.luwu.entity.Connection;
import everyos.bot.luwu.entity.Locale;
import everyos.bot.luwu.entity.Member;
import everyos.bot.luwu.entity.Message;

public interface CommandData {
	public Channel getChannel();
	public Message getMessage();
	public Member getInvoker();
	
	public ClientBehaviour getClientBehaviour();
	public Locale getLocale();
	
	public Connection getConnection();
	public Client getClient();
	public BotInstance getBotInstance();
}
