package everyos.bot.luwu.run.command.modules.configuration.prefix;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.ServerFactory;
import reactor.core.publisher.Mono;

public class PrefixServerFactory implements ServerFactory<PrefixServer> {

	@Override
	public Mono<PrefixServer> create(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		return Mono.just(new PrefixServer(connection, guild, documents));
	}

}
