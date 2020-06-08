package everyos.discord.luwu.command.utility;

import java.util.HashMap;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.GuildAdapter;
import everyos.discord.luwu.annotation.Help;
import everyos.discord.luwu.command.CategoryEnum;
import everyos.discord.luwu.command.CommandData;
import everyos.discord.luwu.command.ICommand;
import everyos.discord.luwu.command.IGroupCommand;
import everyos.discord.luwu.database.DBArray;
import everyos.discord.luwu.localization.Localization;
import everyos.discord.luwu.localization.LocalizedString;
import everyos.discord.luwu.parser.ArgumentParser;
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.AutoRoleCommandHelp, ehelp = LocalizedString.AutoRoleCommandExtendedHelp, category=CategoryEnum.Utility)
public class AutoRoleCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

	public AutoRoleCommand() {
	 	HashMap<String, ICommand> commands;
	    lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

	    //Commands
	    ICommand roleAddCommand = new AutoRoleAddCommand();
	    ICommand roleRemoveCommand = new AutoRoleRemoveCommand();

	    //en_US
	    commands = new HashMap<String, ICommand>();
	    commands.put("add", roleAddCommand);
	    commands.put("remove", roleRemoveCommand);
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

	@Override public HashMap<String, ICommand> getCommands(Localization locale) {
		return lcommands.get(locale);
	}
}

class AutoRoleAddCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getAuthorAsMember()
				.flatMap(m->PermissionUtil.check(m, Permission.MANAGE_ROLES))
				.flatMap(o->message.getGuild())
				.flatMap(guild->{
					ArgumentParser parser = new ArgumentParser(argument);
					
					if (!parser.couldBeRoleID()) return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage)).then(Mono.empty());
					
					return GuildAdapter.of(data.bot, guild).getDocument().flatMap(doc->{
						DBArray array = doc.getObject().getOrCreateArray("aroles", ()->new DBArray());
						if (array.getLength()>=3) {
							return channel.createMessage(data.localize(LocalizedString.TooManyRoles));
						}
						
						array.add(parser.eatRoleID());
						
						return doc.save().then(channel.createMessage(data.localize(LocalizedString.RoleAdded)));
					});
				});
		});
	}
}

class AutoRoleRemoveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getAuthorAsMember()
				.flatMap(m->PermissionUtil.check(m, Permission.MANAGE_ROLES))
				.flatMap(o->message.getGuild())
				.flatMap(guild->{
					ArgumentParser parser = new ArgumentParser(argument);
					
					if (!parser.couldBeRoleID()) return channel.createMessage(data.localize(LocalizedString.UnrecognizedUsage)).then(Mono.empty());
					
					GuildAdapter.of(data.bot, guild).getDocument().flatMap(doc->{
						DBArray array = doc.getObject().getOrDefaultArray("aroles", new DBArray());
						array.removeFirst(parser.eatRoleID());
						
						return doc.save();
					});
					
					return channel.createMessage(data.localize(LocalizedString.RoleRemoved));
				});
		});
	}
}