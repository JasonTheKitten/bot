package everyos.discord.luwu.command.utility;

import java.util.ArrayList;
import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji.Unicode;
import everyos.discord.luwu.Constants;
import everyos.discord.luwu.adapter.TimedExecutionAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBObject;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.ErrorUtil.LocalizedException;
import reactor.core.publisher.Mono;
import xyz.downgoon.snowflake.Snowflake;

@Help(help=LocalizedString.GiveawayCommandHelp, ehelp = LocalizedString.GiveawayCommandExtendedHelp, category=CategoryEnum.Utility)
public class GiveawayCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

	public GiveawayCommand() {
	 	HashMap<String, ICommand> commands;
	    lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

	    //Commands
	    ICommand createCommand = new GiveawayCreateCommand();

	    //en_US
	    commands = new HashMap<String, ICommand>();
	    commands.put("create", createCommand);
	    lcommands.put(Localization.en_US, commands);
	}

	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		if (argument.equals("")) argument = "";

		String cmd = ArgumentParser.getCommand(argument);
		String arg = ArgumentParser.getArgument(argument);

		ICommand command = lcommands.get(data.locale.locale).get(cmd);

		if (command==null)
			return message.getChannel().flatMap(c->c.createMessage(data.localize(LocalizedString.NoSuchSubcommand)));

		return command.execute(message, data, arg);
	}

	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

class GiveawayCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().cast(GuildMessageChannel.class).flatMap(channel->{
			if (argument.isEmpty()) {
				return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
			}
			
			int winners = -1;
			int feth = -1;
			int level = -1;
			int messages = -1;
			String prize = null;
			ArrayList<String> req = new ArrayList<String>();
			boolean reqboost = false;
			long server = -1L;
			long end = 0;
			long jointime = 0;
			
			ArgumentParser parser = new ArgumentParser(argument);
			while (!parser.isEmpty()) {
				if (parser.next().equals("--feth")||parser.next().equals("-f")) {
					parser.eat();
					if (parser.isNumerical()) {
						feth = (int) parser.eatNumerical();
					} else return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
				} else if (parser.next().equals("--level")||parser.next().equals("-l")) {
					parser.eat();
					if (parser.isNumerical()) {
						level = (int) parser.eatNumerical();
					} else return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
				} else if (parser.next().equals("--winners")||parser.next().equals("-l")) {
					parser.eat();
					if (parser.isNumerical()) {
						winners = (int) parser.eatNumerical();
					} else return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
				} else if (parser.next().equals("--messages")||parser.next().equals("-m")) {
					parser.eat();
					if (parser.isNumerical()) {
						messages = (int) parser.eatNumerical();
					} else return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
				} else if (parser.next().equals("--requirement")||parser.next().equals("--req")||parser.next().equals("-r")) {
					//parser.eatQuoted()
				} else if (parser.next().equals("--server")||parser.next().equals("-s")) {
					parser.eat();
					if (parser.isNumerical()) {
						server = parser.eatNumerical();
					} else return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
				} else if (parser.next().equals("--boost")||parser.next().equals("-b")) {
					parser.eat();
					reqboost = true;
				} else if (parser.next().equals("--jointime")||parser.next().equals("-jt")) {
					parser.eat();
					//TODO
				} else {
					return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
				}
			}
			
			if (feth==-1) feth=0;
			if (level==-1) level=0;
			if (messages==-1) messages=0;
			
			GiveawayFormat format = new GiveawayFormat();
			format.server = server;
			
			format.feth = feth;
			format.messages = messages;
			format.level = level;
			
			return sendGiveaway(channel, data, new GiveawayFormat());
		});
	}
	
	public Mono<?> sendGiveaway(GuildMessageChannel channel, CommandData data, GiveawayFormat format) {
		return TimedExecutionAdapter.of(data.bot, new Snowflake(0, 0).nextId()).getDocument().flatMap(doc->{
			DBObject obj = doc.getObject();
			obj.set("type", Constants.GIVEAWAY_ID);
			obj.set("timeout", format.endTime);
			
			obj.set("gid", channel.getGuildId().asLong());
			obj.set("cid", channel.getId().asLong());
			
			obj.set("prize", format.prize);
			obj.set("winners", format.winners);
			
			return doc.save();
		}).then(channel.createEmbed(embed->{
			embed.setTitle(data.localize(LocalizedString.GiveawayTitle));
			embed.setDescription(data.localize(LocalizedString.GiveawayPrompt));
		}).flatMap(embed->{
			return embed.addReaction(Unicode.unicode("\uD83C\uDF89"));
		}));
	}
	
	public class GiveawayFormat {
		public int feth;
		public int level;
		public int messages;
		public int winners;
		public String prize;
		public String[] req;
		public boolean reqBoost;
		public long joinTime;
		public long endTime;
		public long server;
	}
}