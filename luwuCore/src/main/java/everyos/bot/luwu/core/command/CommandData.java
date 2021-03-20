package everyos.bot.luwu.core.command;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Client;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Member;
import everyos.bot.luwu.core.entity.Message;

public class CommandData {
	private Message message;
	private Member sender;
	private Channel channel;
	private Shard shard;
	private Locale locale;
	
	public CommandData(Message message, Member sender, Channel channel, Shard shard) {
		this.message = message;
		this.sender = sender;
		this.channel = channel;
		this.shard = shard;
		this.locale = determineLocale(sender, channel);
	}

	public Channel getChannel() {
		return channel;
	}
	public Message getMessage() {
		return message;
	}
	public Member getInvoker() {
		return sender;
	}
	public Shard getShard() {
		return shard;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public Connection getConnection() {
		return message.getConnection();
	}

	public Client getClient() {
		return message.getClient();
	}

	public BotEngine getBotEngine() {
		return getClient().getBotEngine();
	}
	
	private Locale determineLocale(Member sender, Channel channel) {
		BotEngine engine = sender.getClient().getBotEngine();
		return engine.getLocale(engine.getDefaultLocaleName());
	}
}