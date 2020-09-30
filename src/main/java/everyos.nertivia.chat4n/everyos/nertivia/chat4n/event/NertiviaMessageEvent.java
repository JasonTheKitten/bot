package everyos.nertivia.chat4n.event;

import everyos.bot.chat4j.ChatConnection;
import everyos.bot.chat4j.event.ChatMessageEvent;

public class NertiviaMessageEvent extends NertiviaEvent implements ChatMessageEvent {
	public NertiviaMessageEvent(ChatConnection connection) {
		super(connection);
	}
}
