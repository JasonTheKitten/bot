package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import everyos.discord.exobot.ChannelObject;
import everyos.discord.exobot.GuildObject;
import everyos.discord.exobot.cases.ChannelCase;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.MessageHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class BotIncludeCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember()).isOpted()) {
			ChannelHelper.getChannelData(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block())
				.send("User is not opted to use this command", true);
			
			return;
		}
		
		String[] args = StringUtil.split(argument, " ");
		if (args.length == 0) {
			MessageHelper.send(message.getChannel(), "One argument expected!", true); return;
		}
		
		Guild guild = message.getGuild().block();
		GuildObject guildData = GuildHelper.getGuildData(guild);
		
		ChannelObject from = guildData.getChannel(ChannelHelper.parseChannelId(args[0]));
		
		if (from.CASE == ChannelCase.CASES.DISABLED) {
			from.CASE = ChannelCase.CASES.NULL;
			MessageHelper.send(message.getChannel(), "Re-enabled bot commands on channel", true);
		} else {
			MessageHelper.send(message.getChannel(), "Channel already enabled!", true);
		}
	}

	@Override public String getHelp() {
		return "<enabled-channel> Re-enables bot commands on specified channel";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Configuration;
	}

	@Override public String getFullHelp() {
		return "<enabled-channel> The channel to enable";
	}
}
