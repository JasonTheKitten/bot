package everyos.bot.luwu.command.modules.chatlink;

import everyos.bot.luwu.BotInstance;
import everyos.bot.luwu.entity.Channel;
import everyos.bot.luwu.entity.ChannelID;
import reactor.core.publisher.Mono;

public class ChatLink {
	public static Mono<ChatLink> getByName(BotInstance bot, String name) {
		return Mono.empty();
	}
	public static Mono<ChatLink> getByID(BotInstance bot, long id) {
		return Mono.empty();
	}
	
	public Mono<ChatLinkChannel> configure(Channel channel) {
		return Mono.empty();
	}
	public Mono<Void> sendMessage() {
		return Mono.empty();
	}
	
	public Mono<Void> addChannel(ChannelID channel) {
		return Mono.empty();
	}
	public Mono<Void> removeChannel(ChannelID channel) {
		return Mono.empty();
	}
	@SuppressWarnings("unused")
	private Mono<Void> deleteLink() {
		return Mono.empty();
	}
	public boolean isAutoVerify() {
		// TODO Auto-generated method stub
		return false;
	}
}
