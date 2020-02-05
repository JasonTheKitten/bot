package everyos.discord.exobot.commands;

import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.cases.ChannelCase;
import everyos.discord.exobot.cases.SentenceGameChannelCaseData;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.MessageHelper;
import everyos.discord.exobot.util.UserHelper;

public class SentenceGameCommand implements ICommand {
	@Override public void execute(Message message, String argument) {
		if (!UserHelper.getUserData(GuildHelper.getGuildData(message.getGuild()), message.getAuthorAsMember()).isOpted()) {
			ChannelHelper.getChannelData(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block())
				.send("User is not opted to use this command", true);
			
			return;
		}
		
		Guild guild = message.getGuild().block();
		GuildObject guildData = GuildHelper.getGuildData(guild);
		
		ChannelObject game = ChannelHelper.getChannelData(guildData, guild.createTextChannel(channel->{
            channel
                .setName("one-word-sentence")
                .setTopic("An instance of the one-word sentence game")
                .setReason("User-requested operation");
        }).block());

		game.CASE = ChannelCase.CASES.SENTENCEGAME;
		game.data = new SentenceGameChannelCaseData();
		StaticFunctions.save();
		MessageHelper.send(message.getChannel(), "Channel ready!", true); return;
	}

	@Override public String getHelp() {
		return " Creates a new one-word sentence game channel";
	}

	@Override public COMMANDS getType() {
		return COMMANDS.Fun;
	}
	
	@Override public String getFullHelp() {
        return " Create a new one-word sentence game channel." + 
            "Players can type up to one one-word command (containing only alphanumeric characters) in a row. It will be added to the sentence chain.";
	}
}
