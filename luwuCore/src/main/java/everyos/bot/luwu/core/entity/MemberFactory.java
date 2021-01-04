package everyos.bot.luwu.core.entity;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.luwu.core.database.DBDocument;

public interface MemberFactory<T> {
	T create(Connection connection, ChatMember member, Map<String, DBDocument> documents);
}
