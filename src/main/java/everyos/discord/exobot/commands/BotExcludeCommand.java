package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.cases.ChannelCase;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.MessageHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class BotExcludeCommand implements ICommand {
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
		
		if (from.CASE == ChannelCase.CASES.NULL) {
			from.CASE = ChannelCase.CASES.DISABLED;
			StaticFunctions.save();
			MessageHelper.send(message.getChannel(), "Disabled commands on channel", true);
			StaticFunctions.save();
		} else if (from.CASE == ChannelCase.CASES.DISABLED) {
			MessageHelper.send(message.getChannel(), "Commands on channel already disabled!", true);
		} else MessageHelper.send(message.getChannel(), "Channel has special configurations; Please disable them first.", true);
	}

	@Override public String getHelp() {
		return "<disabled-channel> Disables bot commands on specified channel";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Configuration;
	}

	@Override public String getFullHelp() {
		return "**<disabled-channel>** The channel to disable";
	}
}
