package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.UserObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class UnoptCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		ChannelObject channel = ChannelHelper.getChannelData(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block());
		if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember()).isOpted()) {
			channel.send("User is not opted to use this command", true); return;
		}
		UserObject user = UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), UserHelper.parseUserId(
			StringUtil.split(argument, " ")[0]
		));
		user.opted=false;
		StaticFunctions.save();
		if (user.isHigherThanBot()) {
			channel.send("The user is still opted because the have a role above the bot's. The user will no longer be opted once this role is removed.", true);
		} else {
			channel.send("The user is no longer opted", true);
		}
	}

	@Override public String getHelp() {
		return "<user> Unopts a user to manage the bot";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Configuration;
	}
	
	@Override public String getFullHelp() {
		return "<user> The user to unopt";
	}
}
