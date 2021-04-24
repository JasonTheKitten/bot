package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatServerOutageEvent;
import everyos.bot.luwu.core.entity.Connection;

public class ServerOutageEvent extends ServerEvent {
    public ServerOutageEvent(Connection connection, ChatServerOutageEvent event) {
        super(connection, event);
    }
}
