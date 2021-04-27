package everyos.bot.luwu.run.command.modules.tickets;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.ServerFactory;
import reactor.core.publisher.Mono;

public class TicketServerFactory implements ServerFactory<TicketServer> {
	@Override
	public Mono<TicketServer> create(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		return Mono.just(new TicketServer(connection, guild, documents));
	}
}
