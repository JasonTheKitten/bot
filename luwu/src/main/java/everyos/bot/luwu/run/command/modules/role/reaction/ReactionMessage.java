package everyos.bot.luwu.run.command.modules.role.reaction;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.EmojiID;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.entity.MessageFactory;
import everyos.bot.luwu.core.entity.RoleID;
import reactor.core.publisher.Mono;

public class ReactionMessage extends Message {
	
	public static MessageFactory<ReactionMessage> type = new ReactionMessageFactory();
	
	public ReactionMessage(Connection connection, ChatMessage message, Map<String, DBDocument> documents) {
		super(connection, message, documents);
	}
	public ReactionMessage(Connection connection, ChatMessage message) {
		super(connection, message);
	}
	
	public Mono<Void> editInfo(Consumer<ReactionMessageEditSpec> func) {
		return getGlobalDocument().flatMap(document->{
			DBObject object = document.getObject();
			
			func.accept(new ReactionMessageEditSpec() {
				@Override
				public void addReaction(EmojiID emoji, RoleID role) {
					DBObject roles = object.getOrCreateObject("roles", obj->{});
					roles.set(emoji.toString(), role.getLong());
				}
			});
			
			return document.save();
		});
	}
	public Mono<ReactionInfo> getInfo() {
		return getGlobalDocument().map(document->{
			DBObject object = document.getObject();
			DBObject roles = object.getOrCreateObject("roles", obj->{});
			
			return new ReactionInfo() {
				@Override
				public Optional<RoleID> getReaction(EmojiID emoji) {
					long roleID = roles.getOrDefaultLong(emoji.toString(), -1L);
					if (roleID == -1L) return Optional.empty();
					return Optional.of(new RoleID(getConnection(), roleID));
				}
			};
		});
	}
}
