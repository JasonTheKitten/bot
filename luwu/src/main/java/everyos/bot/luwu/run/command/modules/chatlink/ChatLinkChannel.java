package everyos.bot.luwu.run.command.modules.chatlink;

import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class ChatLinkChannel extends Channel {
	ChatLinkChannel(Connection connection, ChatChannel channel) {
		super(connection, channel);
	}
	public Mono<Long> getLinkID() {
		return getDocument().map(doc->{
			DBObject data = doc.getObject().getOrDefaultObject("data", null);
			System.out.println(data.getOrDefaultLong("chatlinkid", -1L));
			return data.getOrDefaultLong("chatlinkid", -1L);
		});
	};
	public Mono<ChatLink> getLink() {
		return getLinkID().flatMap(id->ChatLink.getByID(getClient().getBotEngine(), id));
	}
	
	public Mono<Void> edit(Consumer<ChatLinkEditSpec> func) {
		return getDocument().flatMap(document->{
			DBObject channelData = document.getObject().getOrDefaultObject("data", null);
			if (channelData==null) return Mono.error(new Exception("Channel data was null"));
			func.accept(new ChatLinkEditSpec() {
				@Override public void setVerified(boolean b) {
					if (b) {
						channelData.set("verified", b);
					} else {
						channelData.remove("verified");
					}
				}
			});
			return document.save();
		});
	}
	
	public static ChatLinkChannelFactory type = new ChatLinkChannelFactory();
}
