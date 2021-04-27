package everyos.bot.chat4j.entity;

import java.util.function.Consumer;

import everyos.bot.chat4j.functionality.ChatInterfaceProvider;
import everyos.bot.chat4j.functionality.channel.ChannelCreateSpec;
import reactor.core.publisher.Mono;

public interface ChatGuild  extends ChatInterfaceProvider {
	/**
	 * Get an ID representing this guild
	 * @return An ID representing this guild
	 */
	long getID();

	String getName();
	
	Mono<ChatChannel> createChannel(Consumer<ChannelCreateSpec> func);
}
