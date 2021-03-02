package everyos.bot.luwu.run.command.modules.starboard;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.MessageFactory;
import reactor.core.publisher.Mono;

public class StarboardMessageFactory implements MessageFactory<StarboardMessage> {

	@Override
	public Mono<StarboardMessage> createMessage(Connection connection, ChatMessage message, Map<String, DBDocument> documents) {
		return Mono.just(new StarboardMessage(connection, message, documents));
	}
}
