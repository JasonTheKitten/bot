package everyos.bot.luwu.run.command.modules.currency;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.MemberFactory;

public class FethMemberFactory implements MemberFactory<FethMember> {
	@Override
	public FethMember create(Connection connection, ChatMember member, Map<String, DBDocument> documents) {
		return new FethMember(connection, member, documents);
	}
}
