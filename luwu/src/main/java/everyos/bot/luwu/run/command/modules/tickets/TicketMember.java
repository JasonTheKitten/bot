package everyos.bot.luwu.run.command.modules.tickets;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Member;

public class TicketMember extends Member {
	public TicketMember(Connection connection, ChatMember member, Map<String, DBDocument> documents) {
		super(connection, member, documents);
	}
}
