package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatServerEvent;
import everyos.bot.luwu.core.entity.Connection;

public class ServerDeleteEvent extends ServerEvent {
    public ServerDeleteEvent(Connection connection, ChatServerEvent event) {
		super(connection, event);
	}
}
