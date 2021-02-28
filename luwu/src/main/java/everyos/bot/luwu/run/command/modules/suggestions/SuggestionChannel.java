package everyos.bot.luwu.run.command.modules.suggestions;

import java.util.Map;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class SuggestionChannel extends Channel {
	public SuggestionChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		super(connection, channel, documents);
	}
	
	public Mono<Channel> getOutputChannel() {
		return getGlobalDocument().flatMap(doc->{
			DBObject data = doc.getObject().getOrCreateObject("data", obj->{});
			long ocid = data.getOrDefaultLong("out", -1L);
			return new ChannelID(getConnection(), ocid, getClient().getID()).getChannel();
		});
	}

	public static SuggestionChannelFactory type = new SuggestionChannelFactory();

	public Mono<Void> setOutput(ChannelID outputChannelID) {
		return getGlobalDocument().flatMap(doc->{
			doc.getObject().set("type", "suggestions");
			DBObject data = doc.getObject().getOrCreateObject("data", obj->{});
			data.set("out", outputChannelID.getLong());
			return doc.save();
		});
	}
}
