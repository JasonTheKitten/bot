package everyos.bot.luwu.run.command.modules.chatlink;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import reactor.core.publisher.Mono;

public class ChatLink {
	public static Mono<ChatLink> getByName(BotEngine bot, String name) {
		return Mono.empty();
	}
	public static Mono<ChatLink> getByID(BotEngine bot, long id) {
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
