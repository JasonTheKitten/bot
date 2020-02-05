package everyos.discord.exobot.cases;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import everyos.discord.exobot.StaticFunctions;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.StringUtil;

public class ChannelCase {
	public static CASES getSpecial(GuildObject guild, MessageChannel channel) {
		return ChannelHelper.getChannelData(guild, channel).CASE;
	}
	public static void execute(CASES special, Message message) {
		GuildObject guild = GuildHelper.getGuildData(message.getGuild());
		ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
		switch(special) {
			case SUGGESTIONS:
				if (message.getAuthor().get().isBot()) return;
				String msg = message.getContent().get();
				message.delete().subscribe();
				
				ChannelObject voteChannel = ChannelHelper.getChannelData(guild, ((SuggestionChannelCaseData) channel.data).voteChannel);
				Message sug = voteChannel.sendThen(embed->{
					embed.setTitle("Suggestion by "+message.getAuthor().get().getUsername());
					embed.setDescription(msg);
					embed.setFooter("Use the reactions to vote!", null);
				});
				voteChannel.sendThen(message.getAuthor().get().getMention(), true).delete().subscribe();
				
				sug.addReaction(ReactionEmoji.unicode("\u2611")).subscribe();
				sug.addReaction(ReactionEmoji.unicode("\u274C")).subscribe();
                break;

            case SENTENCEGAME:
                if (message.getAuthor().get().isBot()) return;
                String content = message.getContent().orElse("").replace("@", "");
                SentenceGameChannelCaseData data = (SentenceGameChannelCaseData) channel.data;
                long uid = message.getAuthor().get().getId().asLong();

                System.out.println(content.isEmpty());
                if (content.isEmpty() || !content.matches("^[a-zA-Z0-9]*$") || data.lastUser==uid) {
                    message.delete().subscribe();
                    return;
                }

                data.sentence+=content+" ";
                data.lastUser = uid;

                StaticFunctions.save();

                message.getChannel().subscribe(e->{
                    e.createMessage("The new sentence is:").subscribe();
                    for (int i=0; i<data.sentence.length(); i+=2000)
                        e.createMessage(StringUtil.sub(data.sentence, i, i+1999)).block();
                });

                break;

			default: break;
		}
	}
	public static enum CASES {
		NULL, DISABLED, SUGGESTIONS, SENTENCEGAME
	}
}
