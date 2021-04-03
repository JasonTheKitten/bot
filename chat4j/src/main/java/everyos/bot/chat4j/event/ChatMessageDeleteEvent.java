package everyos.bot.chat4j.event;

import java.util.Optional;

public interface ChatMessageDeleteEvent extends ChatMessageEvent {
	public Optional<String> getOldMessageContent();
}
