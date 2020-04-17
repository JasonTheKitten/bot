package everyos.discord.bot.command.utility;

import java.util.HashMap;
import java.util.HashSet;

import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;
import discord4j.rest.util.Snowflake;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.adapter.MemberAdapter;
import everyos.discord.bot.annotation.Help;
import everyos.discord.bot.command.CategoryEnum;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import everyos.discord.bot.util.PermissionUtil;
import reactor.core.publisher.Mono;

@Help(help=LocalizedString.TicketCommandHelp, ehelp = LocalizedString.TicketCommandExtendedHelp, category=CategoryEnum.Utility)
public class TicketCommand implements IGroupCommand {
	private HashMap<Localization, HashMap<String, ICommand>> lcommands;

	public TicketCommand() {
	 	HashMap<String, ICommand> commands;
	    lcommands = new HashMap<Localization, HashMap<String, ICommand>>();

	    //Commands
	    ICommand createCommand = new TicketCreateCommand();
	    ICommand setMessageCommand = new SetMessageCommand();

	    //en_US
	    commands = new HashMap<String, ICommand>();
	    commands.put("create", createCommand);
	    commands.put("setmessage", setMessageCommand);
	    //TODO: Reaction command
	    lcommands.put(Localization.en_US, commands);
	}
	
	@Override public Mono<?> execute(Message message, CommandData data, String rargument) {
		return message.getChannel().flatMap(channel->{
			if (!(channel instanceof GuildMessageChannel))
				return channel.createMessage(data.localize(LocalizedString.MustBeInServer));
			
			String argument = rargument;
			if (argument.equals("")) argument = "create";

			String cmd = ArgumentParser.getCommand(argument);
			String arg = ArgumentParser.getArgument(argument);

			ICommand command = lcommands.get(data.locale.locale).get(cmd);

			if (command==null)
				return message.getChannel().flatMap(c->c.createMessage(data.locale.localize(LocalizedString.NoSuchSubcommand)));

			return command.execute(message, data, arg);
		});
	}
	
	@Override public HashMap<String, ICommand> getCommands(Localization locale) { return lcommands.get(locale); }
}

class TicketCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getGuild().flatMap(guild->{
			boolean ticketAlreadyExists = MemberAdapter.of(data.shard, guild, message.getAuthor().get()).getData((obj, doc)->{
				Boolean tae = obj.getOrDefaultBoolean("ticket", false);
				obj.set("ticket", true);
				
				doc.save();
				return tae;
			});
			
			if (ticketAlreadyExists) return message.getChannel()
				.flatMap(c->c.createMessage(data.localize(LocalizedString.TicketAlreadyExists)))
				.then(Mono.empty());
			
			return guild.createTextChannel(channel->{
				channel.setName("Ticket-x");
				channel.setReason("Opened by user "+message.getAuthor().get().getId().asLong());
				channel.setTopic("Ticket opened by user.");
				
				HashSet<PermissionOverwrite> set = new HashSet<PermissionOverwrite>();
				set.add(PermissionOverwrite.forRole(
						guild.getId(),
						PermissionSet.of(0),
						PermissionSet.all()));
				set.add(PermissionOverwrite.forMember(
						Snowflake.of(data.shard.clientID),
						PermissionSet.all(),
						PermissionSet.none()));
				set.add(PermissionOverwrite.forMember(
					message.getAuthor().get().getId(),
					PermissionSet.of(379968),
					PermissionSet.none()));
				channel.setPermissionOverwrites(set);
			})
			.flatMap(channel->{
				ChannelAdapter.of(data.shard, channel.getId().asLong()).getData((obj, doc)->{
					obj.set("type", "ticket");
					obj.createObject("data", dataobj->{});
					
					doc.save();
				});
				MemberAdapter.of(data.shard, guild, message.getAuthor().get()).getData((obj, doc)->{
					obj.set("ticketid", channel.getId().asLong());
					
					doc.save();
					return null;
				});
				
				String reason = GuildAdapter.of(data.shard, guild).getDocument().getObject(obj->{
					return obj.getOrDefaultString("message", data.localize(LocalizedString.DefaultTicketMessage));
					//TODO: A class to format this stuff.
				});
				
				return channel.createMessage(reason);
			})
			.flatMap(o->message.getChannel())
			.flatMap(channel->channel.createMessage(data.localize(LocalizedString.TicketCreated)));
		});
	}
}

class SetMessageCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getAuthorAsMember().flatMap(m->PermissionUtil.check(m, Permission.MANAGE_MESSAGES))
				.flatMap(o->message.getGuild()).map(guild->{ //Would rather use .then, but whatever
					GuildAdapter.of(data.shard, guild).getData((obj, doc)->{
						obj.set("message", argument); doc.save();
					});
					
					return channel.createMessage(data.localize(LocalizedString.TicketMessageSet));
				});
		});
	}
}