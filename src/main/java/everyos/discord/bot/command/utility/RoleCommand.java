package everyos.discord.bot.command.utility;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Snowflake;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.storage.database.DBDocument;
import everyos.storage.database.DBObject;
import reactor.core.publisher.Mono;

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
			return message.getChannel().flatMap(c->c.createMessage(data.locale.localize(LocalizedString.NoSuchSubcommand)));

		return command.execute(message, data, arg);
	}
}

class RoleCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
				return message.getGuild().flatMap(guild->{
				//TODO: Check perms
				AtomicReference<Mono<?>> result = new AtomicReference<Mono<?>>();
				
				DBDocument doc = GuildAdapter.of(data.shard, guild).getDocument();
				doc.getObject(obj->{
					//TODO: Limit creatable roles, for the sanity of my disk
					if (obj.getOrDefaultObject("roles", new DBObject()).has(argument)) {
						result.set(channel.createMessage(data.localize(LocalizedString.RoleAlreadyExists))); return;
					}
					result.set(guild.createRole(spec->{
						spec.setName(argument);
						spec.setMentionable(false);
					}).flatMap(role->{
						obj.getOrCreateObject("roles", ()->new DBObject()).set(argument, role.getId().asString());
						doc.save();
						
						return channel.createMessage(data.localize(LocalizedString.RoleCreated));
					}));
				});
				
				return result.get();
			});
		});
	}
}

class RoleTakeCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getGuild().flatMap(guild->{
				AtomicReference<String> roleid = new AtomicReference<String>();
				GuildAdapter.of(data.shard, guild).getDocument().getObject(obj->{
					DBObject roles = obj.getOrDefaultObject("roles", null);
					if (roles == null) return;
					roleid.set(roles.getOrDefaultString(argument, null));
				});
				if (roleid.get()==null) return channel.createMessage(data.localize(LocalizedString.NoSuchRole));
				return message.getAuthorAsMember().flatMap(member->member.addRole(Snowflake.of(roleid.get())))
					.then(channel.createMessage(data.localize(LocalizedString.RoleGiven)));
			});
		});
		//TODO: On-Error
	}
}

class RoleRemoveCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getGuild().flatMap(guild->{
				AtomicReference<String> roleid = new AtomicReference<String>();
				GuildAdapter.of(data.shard, guild).getDocument().getObject(obj->{
					DBObject roles = obj.getOrDefaultObject("roles", null);
					if (roles == null) return;
					roleid.set(roles.getOrDefaultString(argument, null));
				});
				if (roleid.get()==null) return channel.createMessage(data.localize(LocalizedString.NoSuchRole));
				return message.getAuthorAsMember().flatMap(member->member.removeRole(Snowflake.of(roleid.get())))
					.then(channel.createMessage(data.localize(LocalizedString.RoleRemoved)));
			});
		});
		//TODO: On-Error
	}
}