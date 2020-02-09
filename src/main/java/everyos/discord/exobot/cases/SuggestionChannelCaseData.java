package everyos.discord.exobot.cases;

import com.google.gson.JsonObject;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.SaveUtil.JSONObject;

public class SuggestionChannelCaseData implements IChannelCaseData {
    static public void execute(Message message) {
        GuildObject guild = GuildHelper.getGuildData(message.getGuild());
        ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());

        if (message.getAuthor().get().isBot()) return;
        String msg = message.getContent().get();
        message.delete().subscribe();

        ChannelObject voteChannel = ChannelHelper.getChannelData(guild,
                ((SuggestionChannelCaseData) channel.data).voteChannel);
        Message sug = voteChannel.sendThen(embed -> {
            embed.setTitle("Suggestion by " + message.getAuthor().get().getUsername());
            embed.setDescription(msg);
            embed.setFooter("Use the reactions to vote!", null);
        });
        voteChannel.sendThen(message.getAuthor().get().getMention(), true).delete().subscribe();

        sug.addReaction(ReactionEmoji.unicode("\u2611")).subscribe();
        sug.addReaction(ReactionEmoji.unicode("\u274C")).subscribe();
    }
    

    public String voteChannel;
	
	public SuggestionChannelCaseData(ChannelObject voteChannel) {
		this.voteChannel = voteChannel.id;
	}
	public SuggestionChannelCaseData(JsonObject save) {
		this.voteChannel = save.get("votechannel").getAsString();
    }

	@Override public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("votechannel", this.voteChannel);
        return save;
	}
}
