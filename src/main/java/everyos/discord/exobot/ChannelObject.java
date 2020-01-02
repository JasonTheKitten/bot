package everyos.discord.exobot;

import java.util.function.Consumer;

import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import everyos.discord.exobot.cases.ChannelCase;
import everyos.discord.exobot.cases.IChannelCaseData;
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

	public Message send(String msg, boolean permitPing) {
		return MessageHelper.send((MessageChannel) this.channel, msg, permitPing);
	}
	public Message send(Consumer<? super EmbedCreateSpec> embed) {
		return MessageHelper.send((MessageChannel) this.channel, embed);
	}

	public String serializeSave() {
		return "{\"id\":\""+id+"\"}"; //TODO: Special data
	}
}
