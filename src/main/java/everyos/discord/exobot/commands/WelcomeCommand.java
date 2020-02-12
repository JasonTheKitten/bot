package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class WelcomeCommand implements ICommand {
    @Override public void execute(Message message, String argument) {
        ChannelObject channel = ChannelHelper.getChannelData(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block());
        if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember()).isOpted()) {
			channel.send("User is not opted to use this command", true); return;
		}
            
		Guild rawguild = message.getGuild().block();
        GuildObject guild = GuildHelper.getGuildData(rawguild);
        
		if (argument.equals("")) {
            guild.welcomeChannelID = null;
            guild.welcomeMessage = null;
            channel.send("Cleared welcome message!", true);
        } else {
            argument = argument.trim();
            String channelID = StringUtil.split1(argument, " ");
            String msg = StringUtil.split2(argument, " ");
            guild.welcomeChannelID = ChannelHelper.parseChannelId(channelID);
            guild.welcomeMessage = msg;
            channel.send("Set welcome message!", true);
        }

		StaticFunctions.save();
    }

    @Override public String getHelp() {
        return "<channelid><message> Configures a welcome message that users see when they join";
    }

    @Override public String getFullHelp() {
        return "<channelid> The channel to send welcome messages to\n"+
            "<message> The message to send on user entry";
    }

    @Override
    public COMMANDS getType() {
        return COMMANDS.Configuration;
    }
}