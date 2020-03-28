package everyos.discord.bot.command.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.entity.GuildMessageChannel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.util.PermissionSet;
import everyos.discord.bot.adapter.ChannelAdapter;
import everyos.discord.bot.adapter.GuildAdapter;
import everyos.discord.bot.command.CommandData;
import everyos.discord.bot.command.ICommand;
import everyos.discord.bot.command.IGroupCommand;
import everyos.discord.bot.localization.Localization;
import everyos.discord.bot.localization.LocalizedString;
import everyos.discord.bot.parser.ArgumentParser;
import reactor.core.publisher.Mono;

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
}

class TicketCreateCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		//TODO: Users should only be able to open one ticket per guild at a time
		return message.getGuild().flatMap(guild->{
			return guild.createTextChannel(channel->{
				channel.setName("Ticket-x");
				channel.setReason("Opened by user "+message.getAuthor().get().getId().asString());
				channel.setTopic("Ticket opened by user.");
				
				HashSet<PermissionOverwrite> set = new HashSet<PermissionOverwrite>();
				set.add(PermissionOverwrite.forRole(
						guild.getId(),
						PermissionSet.of(0),
						PermissionSet.all()));
				set.add(PermissionOverwrite.forMember(
						data.shard.client.getSelfId().get(),
						PermissionSet.all(),
						PermissionSet.none()));
				set.add(PermissionOverwrite.forMember(
					message.getAuthor().get().getId(),
					PermissionSet.of(379968),
					PermissionSet.none()));
				channel.setPermissionOverwrites(set);
			})
			.flatMap(channel->{
				ChannelAdapter.of(data.shard, channel.getId().asString()).getDocument().getObject((obj, doc)->{
					obj.set("type", "ticket");
					obj.createObject("data", dataobj->{});
					
					doc.save();
				});
				
				AtomicReference<String> reason = new AtomicReference<String>();
				GuildAdapter.of(data.shard, guild).getDocument().getObject(obj->{
					reason.set(obj.getOrDefaultString("message", data.localize(LocalizedString.DefaultTicketMessage)));
					//TODO: A class to format this stuff. Also, localize
				});
				
				return channel.createMessage(reason.get());
			})
			.flatMap(o->message.getChannel())
			.flatMap(channel->channel.createMessage(data.localize(LocalizedString.TicketCreated)));
		});
	}
}

class SetMessageCommand implements ICommand {
	@Override public Mono<?> execute(Message message, CommandData data, String argument) {
		return message.getChannel().flatMap(channel->{
			return message.getGuild().map(guild->{
				//TODO: Permissions
				GuildAdapter.of(data.shard, guild).getDocument().getObject((obj, doc)->{
					obj.set("message", argument); doc.save();
				});
				
				return channel.createMessage(data.localize(LocalizedString.TicketMessageSet));
			});
		});
	}
}