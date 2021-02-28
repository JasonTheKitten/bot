package everyos.bot.luwu.run.command.modules.suggestions;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.ChannelFactory;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class SuggestionChannelFactory implements ChannelFactory<SuggestionChannel> {
	@Override
	public Mono<SuggestionChannel> createChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		return Mono.just(new SuggestionChannel(connection, channel, documents));
	}
}
