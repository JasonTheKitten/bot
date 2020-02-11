package everyos.discord.exobot.cases;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;

public class ChannelCase {
    public static CASES getSpecial(GuildObject guild, MessageChannel channel) {
        return ChannelHelper.getChannelData(guild, channel).CASE;
    }

    public static void execute(CASES special, Message message) {
        
        switch (special) {
            case SUGGESTIONS:
                SuggestionChannelCaseData.execute(message);
                break;

            case SENTENCEGAME:
                SentenceGameChannelCaseData.execute(message);
                break;

            case CHATLINK:
                ChatLinkChannelCaseData.execute(message);

            default:
                break;
        }
    }

    public static enum CASES {
        NULL, DISABLED, SUGGESTIONS, SENTENCEGAME, CHATLINK
    }
}
