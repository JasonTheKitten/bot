package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;

public class IdentifierCommand implements ICommand {
    public IdentifierCommand() {

    }

    @Override public void execute(Message message, String argument) {
        GuildObject guild = GuildHelper.getGuildData(message.getGuild());
        ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
        
        String identifiable = channel.id;
        if (!argument.isEmpty()) identifiable = ChannelHelper.parseChannelId(argument);

        channel.send(guild.id+"-"+identifiable, true);
    }

    @Override public String getHelp() {
        return "[channel] Generates a channel's identifier using the channel's snowflake";
    }

    @Override public String getFullHelp() {
        return null;
    }

    @Override public COMMANDS getType() {
        return null;
    }
}