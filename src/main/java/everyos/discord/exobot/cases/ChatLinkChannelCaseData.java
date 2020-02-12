package everyos.discord.exobot.cases;

import com.google.gson.JsonObject;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import everyos.discord.exobot.objects.ChannelObject;
import everyos.discord.exobot.objects.GuildObject;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.GuildHelper;
import everyos.discord.exobot.util.SaveUtil.JSONObject;

public class ChatLinkChannelCaseData implements IChannelCaseData {
    //TODO: Perhaps support multiple configurations at once? There could be a "global channel"!
    public String guildPair;
    public String channelPair;

    static public void execute(Message message) {
        if (message.getAuthor().get().isBot()) return;

        GuildObject guild = GuildHelper.getGuildData(message.getGuild());
        ChannelObject channel = ChannelHelper.getChannelData(guild, message.getChannel().block());
        ChatLinkChannelCaseData data = (ChatLinkChannelCaseData) channel.data;

        GuildObject otherGuild = GuildHelper.getGuildData(data.guildPair);
        ChannelObject otherChannel = ChannelHelper.getChannelData(otherGuild, data.channelPair);

        if (!(otherChannel.data instanceof ChatLinkChannelCaseData)) {
            channel.send("Other endpoint not configured!", true); return;
        }

        ChatLinkChannelCaseData otherData = (ChatLinkChannelCaseData) otherChannel.data;
        if (!(otherData.guildPair.equals(guild.id)&&otherData.channelPair.equals(channel.id))) {
            channel.send("Other endpoint configured to different channel!", true); return;
        }

        User author = message.getAuthor().get();
        /*if (otherGuild.requireGuild().guild.getBan(author.getId()).block()) {
            otherChannel.send("One message from blocked user", true); return;
        }*/ //TODO
        otherChannel.send(
            author.getUsername()+": "+message.getContent().orElse("<No Content>").replace("@everyone", "everyone").replace("@here", "here")
        , true);
    }
    

	public ChatLinkChannelCaseData(String guildPair, String channelPair) {
        this.channelPair = channelPair;
        this.guildPair = guildPair;
	}
	public ChatLinkChannelCaseData(JsonObject save) {
        this.channelPair = save.get("channelpairid").getAsString();
        this.guildPair = save.get("guildpairid").getAsString();
    }

	@Override public JSONObject serializeSave() {
        JSONObject save = new JSONObject();

        save.put("channelpairid", channelPair);
        save.put("guildpairid", guildPair);
        
        return save;
	}
}
