package everyos.bot.luwu.run.command.modules.chatlink.channel;

import java.util.Map;
import java.util.function.Consumer;

import everyos.bot.chat4j.entity.ChatChannel;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.database.DBObject;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.Connection;
import reactor.core.publisher.Mono;

public class LinkChannel extends Channel {
	
	public LinkChannel(Connection connection, ChatChannel channel, Map<String, DBDocument> documents) {
		super(connection, channel, documents);
	}

	public Mono<LinkChannelInfo> getInfo() {
		return getGlobalDocument().map(channelDoc->{
			DBObject channelData = channelDoc.getObject().getOrCreateObject("data", obj->{});
			return new LinkChannelInfoImp(getConnection().getBotEngine(), channelData);
		});
	}
	
	public Mono<Void> edit(Consumer<LinkChannelEditSpec> func) {
		return getGlobalDocument().flatMap(channelDoc->{
			DBObject channelData = channelDoc.getObject().getOrCreateObject("data", obj->{});
			func.accept(new LinkChannelEditSpecImp(channelData));
			channelDoc.getObject().set("type", "chatlink");
			return channelDoc.save();
		});
	}
	
	public static final LinkChannelFactory type = new LinkChannelFactory();
	
}
