package everyos.bot.luwu.run.command.modules.chatlink.channel;

import java.util.Map;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.run.command.modules.chatlink.link.ChatLink;
import reactor.core.publisher.Mono;

public class ChatLinkChannel extends Channel {
	protected ChatLinkChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		super(connection, channel, documents);
	}
	public Mono<ChatLink> getLink() {
		return getInfo()
			.flatMap(info->ChatLink.getByID(getClient().getBotEngine(), info.getLinkID()));
	}
	
	//TODO: Quality of life: move some of this to a binding for easier use
	
	public Mono<Void> edit(Consumer<ChatLinkChannelEditSpec> func) {
		return getGlobalDocument().flatMap(document->{
			DBObject channelData = document.getObject().getOrDefaultObject("data", null);
			if (channelData==null) return Mono.error(new Exception("Channel data was null"));
			func.accept(new ChatLinkChannelEditSpec() {
				@Override
				public void setVerified(boolean b) {
					if (b) {
						channelData.set("verified", b);
					} else {
						channelData.remove("verified");
					}
				}
	
				@Override
				public void addMutedUser(UserID user) {
					//TODO: Connection ID
					channelData.getOrCreateArray("muted").add(user.getLong());
				}

				@Override
				public void removeMutedUser(UserID user) {
					channelData.getOrCreateArray("muted").removeAll(user.getLong());
				}
			});
			return document.save();
		});
	}
	
	public Mono<ChatLinkChannelInfo> getInfo() {
		return getGlobalDocument().map(document->{
			return new ChatLinkChannelInfo() {
				public long getLinkID() {
					DBObject data = document.getObject().getOrDefaultObject("data", null);
					return data.getOrDefaultLong("chatlinkid", -1L);
				};
				public boolean isVerified() {
					return document.getObject().getOrDefaultObject("data", null).getOrDefaultBoolean("verified", false);
				}
			};
		});
	}

	
	
	public static ChatLinkChannelFactory type = new ChatLinkChannelFactory();
}
