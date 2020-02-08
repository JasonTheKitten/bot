package everyos.discord.exobot.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.reactivestreams.Publisher;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Channel.Type;
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

public class PurgeCommand implements ICommand {
    @Override
    public void execute(Message message, String argument) {
        GuildObject guild = GuildHelper.getGuildData(message.getGuild());
        ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
        if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember())
                .isOpted()) {
            channel.send("User is not opted to use this command", true);
            return;
        }

        String[] args = StringUtil.split(argument, " ");
        if (args.length == 0) {
            channel.send("Expected at least one parameter", true);
            return;
        }

        int messages = 0;
        if (!args[0].equals("all"))
            try {
                messages = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                channel.send("Invalid # of messages", true);
                return;
            }

        ArrayList<ChannelObject> channels = new ArrayList<ChannelObject>();
        HashMap<UserObject, Boolean> users = new HashMap<UserObject, Boolean>();
        Boolean acceptAnyUserUF = true;
        Boolean purgeAllChannels = false;

        if (args.length > 1)
            for (int i = 1; i < args.length; i++) {
                if (UserHelper.isUserId(args[i])) {
                    acceptAnyUserUF = false;
                    users.put(UserHelper.getUserData(guild, args[i]), true);
                } else if (ChannelHelper.isChannelId(args[i])) {
                    ChannelObject nchannel = ChannelHelper.getChannelData(guild, args[i]);
                    if (nchannel.channel.getType() != Channel.Type.GUILD_TEXT) {
                        channel.send("Purging of one of the channel types is not supported at this moment", true);
                        return;
                    }
                    channels.add(nchannel);
                } else if (args[i].equals("+#")) {
                    purgeAllChannels = true;
                } else {
                    channel.send("Invalid parameter: " + args[i], true);
                    return;
                }
            }

        if (purgeAllChannels) {
            channels.clear();
            guild.requireGuild().guild.getChannels().takeWhile(c -> {
                if (c.getType() != Type.GUILD_TEXT)
                    return true;
                channels.add(ChannelHelper.getChannelData(guild, c));
                return true;
            }).blockLast();
        }

        if (channels.size() == 0)
            channels.add(channel);

        final int messagesToPurge = messages;
        final Boolean acceptAnyUser = acceptAnyUserUF;
        channels.forEach(pchannel -> {
            AtomicInteger messagesPurged = new AtomicInteger(0);
            GuildMessageChannel actC = ((GuildMessageChannel) pchannel.channel);
            ArrayList<Snowflake> snowflakes = new ArrayList<Snowflake>();

            Message tmsg = actC.createMessage("This message will automatically be deleted").block();
            Flux<Message> msgs = actC.getMessagesBefore(tmsg.getId());
            tmsg.delete().subscribe();

            msgs.takeWhile(msg -> {
                try {
                    if (acceptAnyUser || (users.get(UserHelper.getUserData(guild, msg.getAuthorAsMember())) != null)) {
                        snowflakes.add(msg.getId());
                        messagesPurged.incrementAndGet();
                    }
                } catch (ClientException e) {
                }

                return messagesToPurge == 0 || messagesPurged.get() < messagesToPurge;
            }).last().subscribe(e -> {
                Publisher<Snowflake> publisher = Flux.fromIterable(snowflakes);
                actC.bulkDelete(publisher).subscribe();
                channel.send("Messages purged!", true);
            });
			//while(actC.bulkDelete(publisher).blockFirst()!=null) {}; //TODO: Delete remaining messages
		});
	}

	@Override public String getHelp() {
		return "<number/all>[users, channels] Purges messages from channels and users";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Moderation;
	}

	@Override public String getFullHelp() {
		return "**<number/all> Number of messages to purge, or \"all\" to purge all messages\n"+
				"**[users, channels] A list of users and channels to include in the purge. Default is all users, current channel. "+
				"To purge all channels, set \"+#\" as a channel argument";
	}
}
