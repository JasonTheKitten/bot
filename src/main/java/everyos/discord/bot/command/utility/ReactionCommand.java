package everyos.discord.bot.command.utility;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji.Unicode;
import discord4j.rest.util.Permission;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.adapter.MessageAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.database.DBObject;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.ErrorUtil.LocalizedException;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.ReactionCommandHelp, ehelp = LocalizedString.ReactionCommandExtendedHelp, category=CategoryEnum.Utility)
public class ReactionCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

	public ReactionCommand() {
	 	HashMap<String, ICommand> commands;
	    lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

	    //Commands
	    ICommand addCommand = new ReactionAddCommand();

	    //en_US
	    commands = new HashMap<String, ICommand>();
	    commands.put("add", addCommand);
	    lcommands.put(Localization.en_US, commands);
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		String cmd = ArgumentParser.getCommand(argument);
		String arg = ArgumentParser.getArgument(argument);

		ICommand command = lcommands.get(data.locale.locale).get(cmd);

		if (command==null)
			return message.getChannel().flatMap(c->c.createMessage(data.localize(LocalizedString.NoSuchSubcommand)));

		return command.execute(message, data, arg);
	}
	
	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

class ReactionAddCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getAuthorAsMember()
				.flatMap(m->PermissionUtil.check(m, Permission.MANAGE_ROLES, Permission.ADD_REACTIONS))
				.flatMap(o->message.getGuild())
				.flatMap(guild->{
					
					AtomicReference<Mono<?>> result = new AtomicReference<Mono<?>>();
					
					ArgumentParser parser = new ArgumentParser(argument);
					boolean isID = parser.couldBeEmojiID();
					String reactID = isID?parser.eatEmojiID():parser.eat(); //TODO
					
					if (!parser.couldBeRoleID()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
					long roleID = parser.eatRoleID();
					
					if (!parser.isNumerical()) return Mono.error(new LocalizedException(LocalizedString.UnrecognizedUsage));
					long messageID = Long.valueOf(parser.eat());
					
					//TODO: Modifiers: Switch, Permanent
					
					return MessageAdapter.of(data.bot, channel, messageID).getDocument().flatMap(doc->{
						//TODO: Limits, for the sanity of the disk
						//TODO: Finish
						DBObject obj = doc.getObject();
						if (obj.getOrDefaultObject("roles", new DBObject()).has(reactID)) {
							return channel.createMessage(data.localize(LocalizedString.RoleAlreadyExists));
						}
						return channel.getMessageById(Snowflake.of(messageID)).flatMap(msg->{
							obj.getOrCreateObject("roles", ()->new DBObject()).set(reactID, roleID);
							return doc.save().then(msg.addReaction(Unicode.of(isID?Long.valueOf(reactID):null, reactID, true))).then(message.delete());
						});
					});
				});
		});
	}
}

class ReactionRemoveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return null;
	}
}