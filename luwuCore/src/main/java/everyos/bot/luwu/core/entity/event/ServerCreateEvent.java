package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatServerEvent;
import everyos.bot.luwu.core.entity.Connection;

public class ServerCreateEvent extends ServerEvent {
    public ServerCreateEvent(Connection connection, ChatServerEvent event) {
		super(connection, event);
	}
}
