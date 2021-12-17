package everyos.bot.luwu.run.command.modules.chatlink.user;

import java.util.Map;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatUser;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.entity.UserFactory;
import reactor.core.publisher.Mono;

public class LinkUser extends User {
	
	private long linkID;

	protected LinkUser(Connection connection, ChatUser user, long linkID, Map<String, DBDocument> documents) {
		super(connection, user, documents);
		
		this.linkID = linkID;
	}

	public Mono<LinkUserInfo> getInfo() {
		return getDocument().map(userDoc->{
			DBObject userData = userDoc.getObject();
			return new LinkUserInfoImp(getConnection().getBotEngine(), userData);
		});
	}
	
	public Mono<Void> edit(Consumer<LinkUserEditSpec> func) {
		return getDocument().flatMap(userDoc->{
			DBObject channelData = userDoc.getObject();
			func.accept(new LinkUserEditSpecImp(channelData));
			return userDoc.save();
		});
	}
	
	private Mono<DBDocument> getDocument() {
		return getConnection().getBotEngine().getDatabase()
			.collection("linkusers")
			.scan()
			.with("uid", getID().getLong())
			.with("lid", linkID)
			.orCreate(document->{});
	}

	public static UserFactory<LinkUser> typeWith(long linkID) {
		return (connection, user, documents) -> Mono.just(new LinkUser(connection, user, linkID, documents));
	}
	
}
