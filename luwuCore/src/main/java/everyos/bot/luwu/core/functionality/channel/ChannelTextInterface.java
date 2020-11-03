package everyos.bot.luwu.core.functionality.channel;

import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.functionality.message.MessageCreateSpec;
import everyos.bot.luwu.core.functionality.Interface;
import reactor.core.publisher.Mono;

public interface ChannelTextInterface extends Interface {
	public Mono<ChatMessage> send(String text);
	public Mono<ChatMessage> send(Consumer<MessageCreateSpec> spec);
}
