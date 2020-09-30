package everyos.bot.luwu.command.modules.chatlink;

import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.database.Database;
import everyos.bot.luwu.entity.Channel;
import reactor.core.publisher.Mono;

public class ChatLinkChannel extends Channel {
	ChatLinkChannel(ChatChannel channel, Database database) {
		super(channel, database);
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
