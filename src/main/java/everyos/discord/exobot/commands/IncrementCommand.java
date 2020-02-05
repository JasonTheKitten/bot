package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.UserHelper;

public class IncrementCommand implements ICommand {
    @Override public void execute(Message message, String argument) {
        GuildObject guild = GuildHelper.getGuildData(message.getGuild());
        ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
        guild.i++;
		UserHelper.getUserData(guild, message.getAuthorAsMember()).i++;
		StaticFunctions.save();
		channel.send("The value of *i* for this server is now "+guild.i, true);
    }

    @Override public String getHelp() {
        return " A fun counting game";
    }

    @Override public String getFullHelp() {
        return " Increments variable *i*";
    }

    @Override public COMMANDS getType() {
        return COMMANDS.Fun;
    }

}