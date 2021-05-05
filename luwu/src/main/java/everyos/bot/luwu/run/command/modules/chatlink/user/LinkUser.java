package everyos.bot.luwu.run.command.modules.chatlink.user;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.User;

public class LinkUser extends User {
	protected LinkUser(Connection connection, ChatUser user, Map<String, DBDocument> documents) {
		super(connection, user, documents);
	}
}
