package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.cases.ChannelCase;
import everyos.discord.exobot.cases.SuggestionChannelCaseData;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.MessageHelper;
import everyos.discord.exobot.util.StringUtil;
import everyos.discord.exobot.util.UserHelper;

public class SuggestionConfigCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember()).isOpted()) {
			ChannelHelper.getChannelData(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block())
				.send("User is not opted to use this command", true);
			
			return;
		}
		
		String[] args = StringUtil.split(argument, " ");
		if (args.length == 0) {
			MessageHelper.send(message.getChannel(), "At least one argument expected!", true); return;
		}
		
		Guild guild = message.getGuild().block();
		GuildObject guildData = GuildHelper.getGuildData(guild);
		
		ChannelObject from = guildData.getChannel(ChannelHelper.parseChannelId(args[0]));
		
		if (args.length == 1) {
			from.CASE = ChannelCase.CASES.NULL;
			StaticFunctions.save();
			MessageHelper.send(message.getChannel(), "Config reset!", true); return;
		}
		
		ChannelObject to = guildData.getChannel(ChannelHelper.parseChannelId(args[1]));
		from.CASE = ChannelCase.CASES.SUGGESTIONS;
		from.data = new SuggestionChannelCaseData(to);
		StaticFunctions.save();
		MessageHelper.send(message.getChannel(), "Channels have been set", true); return;
	}

	@Override public String getHelp() {
		return "<suggestion-channel>[vote-channel] Sets the channels for suggestions. Call with one parameter to reset channel.";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Configuration;
	}
	
	@Override public String getFullHelp() {
		return "**<suggestion-channel>** The channel to set as suggestion channel, or reset if only parameter\n"+
				"**[vote-channel]** The channel to send votes to";
	}
}
