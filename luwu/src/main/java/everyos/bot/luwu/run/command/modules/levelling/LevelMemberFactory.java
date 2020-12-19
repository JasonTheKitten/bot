package everyos.bot.luwu.run.command.modules.levelling;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.MemberFactory;

public class LevelMemberFactory implements MemberFactory<LevelMember> {
	@Override
	public LevelMember create(Connection connection, ChatMember member, Map<String, DBDocument> documents) {
		return new LevelMember(connection, member, documents);
	}
}
