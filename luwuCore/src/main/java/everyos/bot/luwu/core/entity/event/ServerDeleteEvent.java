package everyos.bot.luwu.core.entity.event;

import everyos.bot.chat4j.event.ChatServerDeleteEvent;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Server;
import reactor.core.publisher.Mono;

public class ServerDeleteEvent extends ServerEvent {
    private ChatServerDeleteEvent serverDeleteEvent;

	public ServerDeleteEvent(Connection connection, ChatServerDeleteEvent event) {
		super(connection, event);
		this.serverDeleteEvent = event;
	}
    
    public Mono<Server> getServer() {
    	return serverDeleteEvent.getGuild()
    		.map(server->new Server(getConnection(), server));
    }
}
