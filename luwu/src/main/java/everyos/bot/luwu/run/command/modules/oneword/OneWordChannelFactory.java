package everyos.bot.luwu.run.command.modules.oneword;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.ChannelFactory;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class OneWordChannelFactory implements ChannelFactory<OneWordChannel> {
	@Override
	public Mono<OneWordChannel> createChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		return Mono.just(new OneWordChannel(connection, channel, documents));
	}
}
