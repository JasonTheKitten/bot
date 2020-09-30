package everyos.bot.luwu.command;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.luwu.BotInstance;
import everyos.bot.luwu.client.ClientBehaviour;
import everyos.bot.luwu.database.Database;
import everyos.bot.luwu.entity.Channel;
import everyos.bot.luwu.entity.Client;
import everyos.bot.luwu.entity.Connection;
import everyos.bot.luwu.entity.Locale;
import everyos.bot.luwu.entity.Member;
import everyos.bot.luwu.entity.Message;

public class CommandDataImp implements CommandData {
	private ChatMessage message;
	private ChatMember sender;
	private ChatChannel channel;
	private BotInstance bot;
	private Database database;
	
	public CommandDataImp(BotInstance bot, Database database, ChatMessage message, ChatMember sender, ChatChannel channel) {
		this.bot = bot;
		this.message = message;
		this.sender = sender;
		this.channel = channel;
		this.database = database;
	}
	
	@Override public Channel getChannel() {
		return new Channel(channel, database);
	}
	@Override public Message getMessage() {
		return new Message(message, database);
	}
	@Override public Member getInvoker() {
		return new Member(sender, database);
	}
	
	@Override public Locale getLocale() {
		return new Locale() {
			@Override public String localize(String name, String... args) {
				if (name.equals("command.ban")) return "ban";
				StringBuilder b = new StringBuilder(name);
				for (int i=0; i<args.length; i+=2) {
					b.append(","+args[i]+":"+args[i+1]);
				}
				return b.toString();
			}
		};
	}
	@Override public ClientBehaviour getClientBehaviour() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override public Connection getConnection() {
		return new Connection(bot, message.getConnection());
	}

	@Override public Client getClient() {
		return new Client(bot, message.getClient());
	}

	@Override public BotInstance getBotInstance() {
		return getClient().getBotInstance();
	}
}