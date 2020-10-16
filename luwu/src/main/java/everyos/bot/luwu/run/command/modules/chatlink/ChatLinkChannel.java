package everyos.bot.luwu.run.command.modules.chatlink;

import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class ChatLinkChannel extends Channel {
	ChatLinkChannel(Connection connection, ChatChannel channel) {
		super(connection, channel);
	}
	public long getLinkID() {
		return -1L;
	};
	public Mono<ChatLink> getLink() {
		return Mono.empty();
	}
	
	public Mono<Void> edit(Consumer<ChatLinkEditSpec> func) {
		return null;
	}
	
	public static ChatLinkChannelFactory type = new ChatLinkChannelFactory();
}
