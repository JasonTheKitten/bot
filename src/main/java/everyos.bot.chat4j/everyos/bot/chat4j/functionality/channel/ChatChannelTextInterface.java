package everyos.bot.chat4j.functionality.channel;

import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.chat4j.functionality.ChatInterface;
import everyos.bot.chat4j.functionality.message.MessageCreateSpec;
import reactor.core.publisher.Mono;

public interface ChatChannelTextInterface extends ChatInterface {
	public Mono<ChatMessage> send(String text);
	public Mono<ChatMessage> send(Consumer<MessageCreateSpec> spec);
}
