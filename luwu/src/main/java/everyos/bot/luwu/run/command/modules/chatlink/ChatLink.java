package everyos.bot.luwu.run.command.modules.chatlink;

import java.util.ArrayList;

import com.mongodb.client.model.Filters;

import everyos.bot.chat4j.functionality.channel.ChatChannelTextInterface;
import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.downgoon.snowflake.Snowflake;

public class ChatLink {
	private DBDocument document;
	@SuppressWarnings("unused")
	private BotEngine botEngine;
	private long id;
	
	private ArrayList<ChatLinkChannel> channelCache = new ArrayList<>();
	
	public ChatLink(BotEngine botEngine, DBDocument document) {
		this.botEngine = botEngine;
		this.document = document;
		this.id = document.getObject().getOrDefaultLong("clid", -1L);
		//data.chatlinkid
	}
	
	public long getID() {
		return id;
	}
	
	public Mono<Void> sendMessage(Message message) {
		ChatLinkChannel[] channels = channelCache.toArray(new ChatLinkChannel[channelCache.size()]);
		ChannelID exclusion = message.getChannelID();
		
		return Flux.fromArray(channels)
			//.filter(channel->!channel.getID().equals(exclusion))
			.flatMap(channel->{
				return sendMessageToChannel(channel, message);
			})
			.then();
	}
	
	private Mono<Void> sendMessageToChannel(Channel channel, Message message) {
		return channel.getInterface(ChatChannelTextInterface.class).send(message.getContent().orElse("Empty message"))
			.then();
	}
	
	public Mono<ChatLinkChannel> addChannel(Channel channel) {
		return channel.as(NewChatLinkChannel.type).join(this)
			.doOnNext(clchannel->channelCache.add(clchannel));
	}
	public Mono<Void> removeChannel(ChannelID channelID) {
		return Mono.just(true).map(b->{
			channelCache.forEach(channel->{
				if (channelID.equals(channel.getID())) {
					channelCache.remove(channel); //TODO: 100% chance of concurrency error
				}
			});
			return null;
		});
	}
	
	public Mono<Void> deleteLink() {
		return document.delete();
	}
	public boolean isAutoVerify() {
		return document.getObject().getOrDefaultBoolean("autoverify", false);
	}
	
	//TODO: Cache chatlink data
	public static Mono<ChatLink> createChatLink(BotEngine engine) {
		return Mono.just(true).flatMap(b->{
			long linkID = new Snowflake(0, 0).nextId();
			return engine.getDatabase().collection("chatlink").scan().filter(Filters.eq("clid", linkID)).orCreate(editSpec->{
				editSpec.set("clid", linkID);
			}).flatMap(doc->doc.save().then(loadFromDB(engine, doc)));
		});
	}
	public static Mono<ChatLink> getByName(BotEngine engine, String name) {
		return engine.getDatabase().collection("chatlink").scan().filter(Filters.eq("name", name)).orEmpty()
			.flatMap(doc->loadFromDB(engine, doc));
	}
	public static Mono<ChatLink> getByID(BotEngine engine, long id) {
		return engine.getDatabase().collection("chatlink").scan().filter(Filters.eq("clid", id)).orEmpty()
			.flatMap(doc->loadFromDB(engine, doc));
	}
	
	private static Mono<ChatLink> loadFromDB(BotEngine engine, DBDocument document) {
		long linkID = document.getObject().getOrDefaultLong("clid", -1L);
		engine.getDatabase().collection("channel").scan()
			.with("data.clid", linkID)
			.with("type", "chatlink")
			.rest()
			.flatMap(channelDocument->{
				int clientID = channelDocument.getObject().getOrDefaultInt("cliid", 0);
				long channelID = channelDocument.getObject().getOrDefaultLong("cid", -1L);
				return engine.getConnectionByID(clientID).getChannelByID(channelID);
			});
		return Mono.just(new ChatLink(engine, document));
	}
}
