package everyos.bot.luwu.core.functionality.channel;

import java.util.function.Consumer;

import everyos.bot.chat4j.functionality.message.MessageCreateSpec;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.functionality.Interface;
import reactor.core.publisher.Mono;

public interface ChannelTextInterface extends Interface {
	public Mono<Message> send(String text);
	public Mono<Message> send(Consumer<MessageCreateSpec> spec);
}
