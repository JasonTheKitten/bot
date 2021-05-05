package everyos.bot.luwu.run.command.modules.chatlink.channel;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.run.command.modules.chatlink.link.ChatLink;
import reactor.core.publisher.Mono;

public class NewChatLinkChannel extends Channel {
	public static NewChatLinkChannelFactory type = new NewChatLinkChannelFactory();

	public NewChatLinkChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		super(connection, channel, documents);
	}
	
	//TODO: Avoid the need for sharing this
	public Mono<ChatLinkChannel> join(ChatLink link) {
		return getGlobalDocument().flatMap(channelDoc->{
			DBObject channelObj = channelDoc.getObject();
			channelObj.set("type", "chatlink");
			DBObject channelData = channelObj.getOrCreateObject("data", obj->{});
			channelData.set("chatlinkid", link.getID());
					
			return channelDoc.save().then(this.as(ChatLinkChannel.type));
		});
	}
}
