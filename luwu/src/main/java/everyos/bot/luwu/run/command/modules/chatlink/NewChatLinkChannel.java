package everyos.bot.luwu.run.command.modules.chatlink;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class NewChatLinkChannel extends Channel {
	public static NewChatLinkChannelFactory type = new NewChatLinkChannelFactory();

	public NewChatLinkChannel(Connection connection, ChatChannel channel) {
		super(connection, channel);
	}
	
	//TODO: Avoid the need for package-wide methods
	protected Mono<ChatLinkChannel> join(ChatLink link) {
		return getDocument().flatMap(channelDoc->{
			DBObject channelObj = channelDoc.getObject();
			channelObj.set("type", "chatlink");
			DBObject channelData = channelObj.getOrCreateObject("data", obj->{});
			channelData.set("chatlinkid", link.getID());
					
			return channelDoc.save();
		}).then(Mono.just(this.as(ChatLinkChannel.type)));
	}
}
