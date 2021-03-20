package everyos.bot.luwu.run.command.modules.starboard;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.ServerFactory;
import reactor.core.publisher.Mono;

public class StarboardServerFactory implements ServerFactory<StarboardServer> {
	@Override
	public Mono<StarboardServer> create(Connection connection, ChatGuild server, Map<String, DBDocument> documents) {
		return Mono.just(new StarboardServer(connection, server, documents));
	}
}
