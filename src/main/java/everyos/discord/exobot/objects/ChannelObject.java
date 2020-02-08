package everyos.discord.exobot.objects;

import java.util.function.Consumer;

import com.google.gson.JsonObject;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import discord4j.core.spec.EmbedCreateSpec;
import everyos.discord.exobot.cases.ChannelCase;
import everyos.discord.exobot.cases.ChannelCase.CASES;
import everyos.discord.exobot.cases.IChannelCaseData;
import everyos.discord.exobot.cases.SentenceGameChannelCaseData;
import everyos.discord.exobot.cases.SuggestionChannelCaseData;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.MessageHelper;
import everyos.discord.exobot.util.SaveUtil.JSONObject;

public class ChannelObject {
	public ChannelCase.CASES CASE = ChannelCase.CASES.NULL;
	public GuildObject guild;
	public Channel channel;
	public String id;
	public IChannelCaseData data;
	
	public ChannelObject(GuildObject guild, Channel channel) {
		this(guild, ChannelHelper.getChannelId(channel));
		this.channel = channel;
    }
    public ChannelObject(GuildObject guild, String cid) {
		this.guild = guild;
		this.id = cid;
    }

	public ChannelObject(GuildObject guild, JsonObject save) {
		this.guild = guild;
        this.id = save.get("id").getAsString();
		try {
			CASE = CASES.valueOf(save.get("case").getAsString());
			if (CASE == CASES.SUGGESTIONS) {
				this.data = new SuggestionChannelCaseData(save.get("casedata").getAsJsonObject());
			} else if (CASE == CASES.SENTENCEGAME) {
                this.data = new SentenceGameChannelCaseData(save.get("casedata").getAsJsonObject());
            }
		} catch (Exception e) {
			e.printStackTrace();
			CASE = ChannelCase.CASES.NULL;
		}
    }
    
    public ChannelObject requireChannel(){
        if (this.channel==null) this.channel = guild.guild.getChannelById(Snowflake.of(this.id)).block();
        return this;
    }

	public void send(String msg, boolean permitPing) {
		MessageHelper.send((MessageChannel) this.requireChannel().channel, msg, permitPing);
	}
	public void send(Consumer<? super EmbedCreateSpec> embed) {
		MessageHelper.send((MessageChannel) this.requireChannel().channel, embed);
    }
    public Message sendThen(String msg, boolean permitPing) {
		return MessageHelper.sendThen((MessageChannel) this.requireChannel().channel, msg, permitPing);
	}
	public Message sendThen(Consumer<? super EmbedCreateSpec> embed) {
		return MessageHelper.sendThen((MessageChannel) this.requireChannel().channel, embed);
	}

	public JSONObject serializeSave() {
        JSONObject save = new JSONObject();
        save.put("id", id);
        save.put("case", CASE.toString());
		save.put("casedata", (data==null?(new JSONObject()):data.serializeSave()));
		return save;
	}
}
