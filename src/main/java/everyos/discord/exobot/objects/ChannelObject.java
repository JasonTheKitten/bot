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
import everyos.discord.exobot.cases.SuggestionChannelCaseData;
import everyos.discord.exobot.util.ChannelHelper;
import everyos.discord.exobot.util.MessageHelper;

public class ChannelObject {
	public ChannelCase.CASES CASE = ChannelCase.CASES.NULL;
	public GuildObject guild;
	public Channel channel;
	public String id;
	public IChannelCaseData data;
	
	public ChannelObject(GuildObject guild, Channel channel) {
		this.guild = guild;
		this.channel = channel;
		this.id = ChannelHelper.getChannelId(channel);
	}

	public ChannelObject(GuildObject guild, JsonObject save) {
		this.guild = guild;
		this.id = save.get("id").getAsString();
		this.channel = guild.guild.getChannelById(Snowflake.of(this.id)).block();
		try {
			CASE = CASES.valueOf(save.get("case").getAsString());
			if (CASE == CASES.SUGGESTIONS) {
				this.data = new SuggestionChannelCaseData(save.get("casedata").getAsJsonObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
			CASE = ChannelCase.CASES.NULL;
		}
	}

	public Message send(String msg, boolean permitPing) {
		return MessageHelper.send((MessageChannel) this.channel, msg, permitPing);
	}
	public Message send(Consumer<? super EmbedCreateSpec> embed) {
		return MessageHelper.send((MessageChannel) this.channel, embed);
	}

	public String serializeSave() {
		StringBuilder serialized = new StringBuilder("{\"id\":\""+id+"\",\"case\":\""+CASE.toString()+"\",\"casedata\":"); //TODO: Special data
		if (data==null) {
			serialized.append("{}");
		} else {
			serialized.append(data.serializeSave());
		}
		serialized.append("}");
		return serialized.toString();
	}
}
