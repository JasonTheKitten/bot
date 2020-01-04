package everyos.discord.exobot.commands;

import java.util.ArrayList;
import java.util.HashMap;

import org.reactivestreams.Publisher;

import discord4j.core.object.entity.GuildMessageChannel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.util.Snowflake;
import discord4j.rest.http.client.ClientException;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.objects.UserObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;
import reactor.core.publisher.Flux;

public class PurgeAfterCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		GuildObject guild = GuildHelper.getGuildData(message.getGuild());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember()).isOpted()) {
			channel.send("User is not opted to use this command", true); return;
		}
		
		String[] args = StringUtil.split(argument, " ");
		if (args.length==0) {
			channel.send("Expected at least one parameter", true); return;
		}
		
		ArrayList<ChannelObject> channels = new ArrayList<ChannelObject>();
		HashMap<UserObject, Boolean> users = new HashMap<UserObject, Boolean>();
		Boolean acceptAnyUserUF = true;
		
		if (args.length>1)
			for (int i=1; i<args.length; i++) {
				if (UserHelper.isUserId(args[i])) {
					acceptAnyUserUF = false;
					users.put(UserHelper.getUserData(guild, args[i]), true);
				}
			}
		
		final Boolean acceptAnyUser = acceptAnyUserUF;
		
		GuildMessageChannel actC = (GuildMessageChannel) channel.channel;
		ArrayList<Snowflake> snowflakes = new ArrayList<Snowflake>();
		
		actC.getMessagesAfter(Snowflake.of(args[0])).takeWhile(msg->{
			try {
				if (acceptAnyUser||(users.get(UserHelper.getUserData(guild, msg.getAuthorAsMember()))!=null)) {
					snowflakes.add(msg.getId());
				}
			} catch (ClientException e) {}
			return true;
		}).blockLast();
		Publisher<Snowflake> publisher = Flux.fromIterable(snowflakes);
		while(actC.bulkDelete(publisher).blockFirst()!=null) {}; //TODO: Delete remaining messages
		
		channel.send("Messages purged!", true);
	}

	@Override public String getHelp() {
		return "<messageid> Purges any messages after specified messages";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Moderation;
	}

	@Override public String getFullHelp() {
		return "**<messageid>** The last message to keep - this message is not deleted.\n"+
				"**[users]** A list of users to include in the purge. Default is all users";
	}
}
