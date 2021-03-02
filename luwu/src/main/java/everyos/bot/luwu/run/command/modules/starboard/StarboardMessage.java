package everyos.bot.luwu.run.command.modules.starboard;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatMessage;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Message;
import reactor.core.publisher.Mono;

public class StarboardMessage extends Message {
	protected StarboardMessage(Connection connection, ChatMessage message, Map<String, DBDocument> documents) {
		super(connection, message, documents);
	}
	
	public static StarboardMessageFactory type = new StarboardMessageFactory();

	public Mono<MessageInfo> getInfo() {
		return null;
	}
}
