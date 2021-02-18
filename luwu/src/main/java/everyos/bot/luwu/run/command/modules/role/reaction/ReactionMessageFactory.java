package everyos.bot.luwu.run.command.modules.role.reaction;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.MessageFactory;
import reactor.core.publisher.Mono;

public class ReactionMessageFactory implements MessageFactory<ReactionMessage> {
	@Override
	public Mono<ReactionMessage> createMessage(Connection connection, ChatMessage message, Map<String, DBDocument> documents) {
		return Mono.just(new ReactionMessage(connection, message, documents));
	}
}
