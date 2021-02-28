package everyos.bot.luwu.run.command.modules.leveling;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.ServerFactory;
import reactor.core.publisher.Mono;

public class LevelServerFactory implements ServerFactory<LevelServer> {

	@Override
	public Mono<LevelServer> create(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		return Mono.just(new LevelServer(connection, guild, documents));
	}
}
