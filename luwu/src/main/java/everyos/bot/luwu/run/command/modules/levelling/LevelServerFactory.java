package everyos.bot.luwu.run.command.modules.levelling;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Server;
import everyos.bot.luwu.core.entity.ServerFactory;

public class LevelServerFactory implements ServerFactory<Server> {

	@Override
	public Server create(Connection connection, ChatGuild guild, Map<String, DBDocument> documents) {
		return new LevelServer(connection, guild, documents);
	}
}
