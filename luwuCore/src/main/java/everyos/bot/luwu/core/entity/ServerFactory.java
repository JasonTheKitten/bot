package everyos.bot.luwu.core.entity;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatGuild;
import everyos.bot.luwu.core.database.DBDocument;

public interface ServerFactory<T extends Server> {
	T create(Connection connection, ChatGuild guild, Map<String, DBDocument> documents);
}
