package everyos.discord.luwu.command.utility;

import java.util.HashMap;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Permission;
import everyos.discord.luwu.adapter.GuildAdapter;
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
import everyos.discord.luwu.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.RoleCommandHelp, ehelp = LocalizedString.RoleCommandExtendedHelp, category=CategoryEnum.Utility)
public class RoleCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

	public RoleCommand() {
	 	HashMap<String, ICommand> commands;
	    lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

	    //Commands
	    ICommand roleCreateCommand = new RoleCreateCommand();
	    ICommand roleTakeCommand = new RoleTakeCommand();
	    ICommand roleRemoveCommand = new RoleRemoveCommand();

	    //en_US
	    commands = new HashMap<String, ICommand>();
	    commands.put("create", roleCreateCommand);
	    commands.put("take", roleTakeCommand);
	    commands.put("remove", roleRemoveCommand);
	    //set, delete, list
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

class RoleCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getAuthorAsMember()
				.flatMap(member->PermissionUtil.check(member, Permission.MANAGE_ROLES))
				.flatMap(o->message.getGuild())
				.flatMap(guild->{
					return GuildAdapter.of(data.bot, guild).getDocument().flatMap(doc->{
						//TODO: Limit creatable roles, for the sanity of my disk
						DBObject obj = doc.getObject();
						if (obj.getOrDefaultObject("roles", new DBObject()).has(argument)) {
							return channel.createMessage(data.localize(LocalizedString.RoleAlreadyExists));
						}
						return guild.createRole(spec->{
							spec.setName(argument);
							spec.setMentionable(false);
						}).flatMap(role->{
							obj.getOrCreateObject("roles", ()->new DBObject()).set(argument, role.getId().asLong());
							return doc.save().then(channel.createMessage(data.localize(LocalizedString.RoleCreated)));
						});
					});
			});
		});
	}
}

class RoleTakeCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getGuild()
				.flatMap(guild->GuildAdapter.of(data.bot, guild).getDocument())
				.flatMap(doc->{
					DBObject roles = doc.getObject().getOrDefaultObject("roles", null);
					if (roles == null) return Mono.error(new LocalizedException(LocalizedString.NoSuchRole));
					long roleID =  roles.getOrDefaultLong(argument, -1L);
					if (roleID==-1L) return Mono.error(new LocalizedException(LocalizedString.NoSuchRole));
					return message.getAuthorAsMember().flatMap(member->member.addRole(Snowflake.of(roleID)))
						.then(channel.createMessage(data.localize(LocalizedString.RoleGiven)));
				});
		});
	}
}

class RoleRemoveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getGuild()
				.flatMap(guild->GuildAdapter.of(data.bot, guild).getDocument())
				.flatMap(doc->{
					DBObject roles = doc.getObject().getOrDefaultObject("roles", null);
					if (roles == null) return Mono.error(new LocalizedException(LocalizedString.NoSuchRole));
					long roleID =  roles.getOrDefaultLong(argument, -1L);
					if (roleID==-1L) return Mono.error(new LocalizedException(LocalizedString.NoSuchRole));
					return message.getAuthorAsMember().flatMap(member->member.removeRole(Snowflake.of(roleID)))
						.then(channel.createMessage(data.localize(LocalizedString.RoleRemoved)));
				});
		});
	}
}