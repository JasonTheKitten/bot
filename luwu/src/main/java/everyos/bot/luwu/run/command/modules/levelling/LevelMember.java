package everyos.bot.luwu.run.command.modules.levelling;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatMember;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Member;
import reactor.core.publisher.Mono;

public class LevelMember extends Member {
	protected LevelMember(Connection connection, ChatMember member, Map<String, DBDocument> documents) {
		super(connection, member, documents);
	}
	
	public Mono<LevelState> getLevelState() {
		return getLocalDocument()
			.map(document->{
				DBObject memberObject = document.getObject();
				DBObject levelObject = memberObject.getOrCreateObject("level", obj->{});
				long xp = levelObject.getOrDefaultLong("xp", 0);
				long timestamp = levelObject.getOrDefaultLong("lmsg", Long.MIN_VALUE);
				
				return new LevelState(xp, timestamp);
			});
	}
	
	public Mono<Void> setLevelState(LevelState state) {
		return getLocalDocument()
			.flatMap(document->{
				DBObject memberObject = document.getObject();
				DBObject levelObject = memberObject.getOrCreateObject("level", obj->{});
				levelObject.set("xp", state.getXPTotal());
				levelObject.set("lmsg", state.getTimestamp());
				
				return document.save();
			});
	}

	public static LevelMemberFactory type = new LevelMemberFactory();
}
