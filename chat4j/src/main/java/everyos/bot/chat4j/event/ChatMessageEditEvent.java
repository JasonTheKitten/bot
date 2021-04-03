package everyos.bot.chat4j.event;

import java.util.Optional;

import everyos.bot.chat4j.entity.ChatMessage;

public interface ChatMessageEditEvent extends ChatMessageEvent {
	Optional<ChatMessage> getOldMessage();
}
