package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class OptCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		ChannelObject channel = ChannelHelper.getChannelData(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block());
		if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember()).isOpted()) {
			channel.send("User is not opted to use this command", true); return;
		}
        String[] args = StringUtil.split(argument, " ");
		if (args.length<1) {
			channel.send("Expected at least one argument!", true); return;
        }
		UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), UserHelper.parseUserId(args[0])).opted=true;
		StaticFunctions.save();
		channel.send("User has been opted", true);
	}

	@Override public String getHelp() {
		return "<User> Opts a user to manage the bot";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Configuration;
	}
	
	@Override public String getFullHelp() {
		return "**<User>** User to opt";
	}
}
