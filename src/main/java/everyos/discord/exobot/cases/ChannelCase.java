package everyos.discord.exobot.cases;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import everyos.discord.exobot.ChannelObject;
import everyos.discord.exobot.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;

public class ChannelCase {
	public static CASES getSpecial(GuildObject guild, MessageChannel channel) {
		return ChannelHelper.getChannelData(guild, channel).CASE;
	}
	public static void execute(CASES special, Message message) {
		ChannelObject channel = ChannelHelper.getChannelData(GuildHelper.getGuildData(message.getGuild()), message.getChannel().block());
		switch(special) {
			case SUGGESTIONS: {
				if (message.getAuthor().get().isBot()) return;
				String msg = message.getContent().get();
				message.delete().block();
				
				Message sug = ((SuggestionChannelCaseData) channel.data).voteChannel.send(embed->{
					embed.setTitle("Suggestion by "+message.getAuthor().get().getUsername());
					embed.setDescription(msg);
					embed.setFooter("Use the reactions to vote!", null);
				});
				((SuggestionChannelCaseData) channel.data).voteChannel.send(message.getAuthor().get().getMention(), true).delete().block();
				
				sug.addReaction(ReactionEmoji.unicode("\u2611")).block();
				sug.addReaction(ReactionEmoji.unicode("\u274C")).block();
				break;
			}
			default: break;
		}
	}
	public static enum CASES {
		NULL, DISABLED, SUGGESTIONS
	}
}
