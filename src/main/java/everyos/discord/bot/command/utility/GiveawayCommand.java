package everyos.discord.bot.command.utility;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji.Unicode;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import reactor.core.publisher.Mono;

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
			return message.getChannel().flatMap(c->c.createMessage(data.locale.localize(LocalizedString.NoSuchSubcommand)));

		return command.execute(message, data, arg);
	}

	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

class GiveawayCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			if (argument.isEmpty()) {
				return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
			}
			
			int feth = -1;
			int level = -1;
			int messages = -1;
			String server = null;
			String req = null;
			boolean reqboost = false; //TODO: Maybe convert to int? For multiple boosts
			boolean boostMustBeNew = false;
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
						server = parser.eat();
					} else return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage));
				} else if (parser.next().equals("--boost")||parser.next().equals("-b")) {
					parser.eat();
					reqboost = true;
				} else if (parser.next().equals("--newboost")||parser.next().equals("-nb")) {
					parser.eat();
					boostMustBeNew = true;
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
			format.feth = feth;
			format.messages = messages;
			format.level = level;
			
			return sendGiveaway(channel, data, new GiveawayFormat());
		});
	}
	
	public Mono<?> sendGiveaway(MessageChannel channel, CommandData data, GiveawayFormat format) {
		return channel.createEmbed(embed->{
			embed.setTitle(data.localize(LocalizedString.GiveawayTitle));
			embed.setDescription(data.localize(LocalizedString.GiveawayPrompt));
		}).flatMap(embed->{
			return embed.addReaction(Unicode.unicode("\uD83C\uDF89"));
		});
	}
	
	public class GiveawayFormat {
		public int feth;
		public int level;
		public int messages;
		public String server;
		public String[] req;
		public boolean reqBoost;
		public long joinTime;
		public long endTime;
	}
}