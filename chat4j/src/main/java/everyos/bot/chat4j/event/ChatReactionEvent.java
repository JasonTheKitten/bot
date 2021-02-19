package everyos.bot.chat4j.event;

import java.util.Optional;

public interface ChatReactionEvent extends ChatMessageEvent {
	public Optional<String> getReactionString();
	public Optional<Long> getReactionLong();
}
