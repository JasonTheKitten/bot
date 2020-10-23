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
	
	public CommandData(Message message, Member sender, Channel channel) {
		this.message = message;
		this.sender = sender;
		this.channel = channel;
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
	
	public Locale getLocale() {
		return new Locale() {
			public String localize(String name, String... args) {
				//TODO: I really need to rig up resource files
				if (name.equals("command.ban")) return "ban";
				if (name.equals("command.link.create")) return "create";
				if (name.equals("command.easteregg.cat")) return "cat";
				if (name.equals("command.hug")) return "hug";
				StringBuilder b = new StringBuilder(name);
				for (int i=0; i<args.length; i+=2) {
					b.append(","+args[i]+":"+args[i+1]);
				}
				return b.toString();
			}
		};
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
}