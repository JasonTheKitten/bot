package everyos.bot.luwu.run.command.modules.chatlink;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.Filters;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.Message;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.downgoon.snowflake.Snowflake;

public class ChatLink {
	//Static Methods
	public static Mono<ChatLink> getByName(BotEngine engine, String name) {
		return engine.getDatabase().collection("chatlinks").scan().filter(Filters.eq("name", name)).orEmpty()
			.flatMap(doc -> loadFromDB(engine, doc));
	}

	public static Mono<ChatLink> getByID(BotEngine engine, long id) {
		return engine.getDatabase().collection("chatlinks").scan().filter(Filters.eq("clid", id)).orEmpty()
			.flatMap(doc -> loadFromDB(engine, doc));
	}
	
	// TODO: Cache chatlink data
	public static Mono<ChatLink> createChatLink(BotEngine engine) {
		return Mono.just(true).flatMap(b -> {
			long linkID = new Snowflake(0, 0).nextId();
			return engine.getDatabase().collection("chatlinks").scan().with("clid", linkID).orCreate(editSpec -> {
			}).flatMap(doc -> doc.save().then(loadFromDB(engine, doc)));
		});
	}

	private static Mono<ChatLink> loadFromDB(BotEngine engine, DBDocument document) {
		long linkID = document.getObject().getOrDefaultLong("clid", -1L);
		// Get channels
		return engine.getDatabase().collection("channels").scan()
			.with("data.chatlinkid", linkID)
			.with("type", "chatlinks").rest().flatMap(channelDocument -> {
				int clientID = channelDocument.getObject().getOrDefaultInt("cliid", 0);
				long channelID = channelDocument.getObject().getOrDefaultLong("cid", -1L);
				return engine.getConnectionByID(clientID).getChannelByID(channelID);
			}).flatMap(channel->channel.as(ChatLinkChannel.type)).collectList()
			// Get link
			.map(list->new ChatLink(engine, document, list));
	}
	
	
	//Main Class
	private DBDocument document;
	private BotEngine botEngine;
	private long id;

	private List<ChatLinkChannel> channelCache = new ArrayList<>();

	public ChatLink(BotEngine botEngine, DBDocument document, List<ChatLinkChannel> channels) {
		this.botEngine = botEngine;
		this.document = document;
		this.id = document.getObject().getOrDefaultLong("clid", -1L);
		this.channelCache = channels;
		// data.chatlinkid
	}

	public long getID() {
		return id;
	}

	public Mono<Void> sendMessage(Message message) {
		return checkMessageSendAllowedForGlobal(message)
			.thenMany(getChannelFlux())
			 .filter(channel->!channel.getID().equals(message.getChannelID()))
			.flatMap(channel -> {
				return sendMessageToChannel(channel, message);
			}).then();
	}
	
	public Mono<Void> sendSystemMessage(String message) {
		Locale locale = getDefaultLocale();
		return getChannelFlux()
			.flatMap(channel->sendTextToChannel(channel, locale.localize("command.link.systemheader", "message", quote(message))))
			.then();
	}

	public Mono<ChatLinkChannel> addChannel(Channel channel) {
		return channel
			.as(NewChatLinkChannel.type)
			.flatMap(clchannel->clchannel.join(this))
			.doOnNext(clchannel -> channelCache.add(clchannel));
	}

	public Mono<Void> removeChannel(ChannelID channelID) {
		return Mono.just(true).map(b -> {
			channelCache.forEach(channel -> {
				if (channelID.equals(channel.getID())) {
					channelCache.remove(channel); // TODO: 100% chance of concurrency error
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
	
	
	//Private methods
	private Mono<Void> sendTextToChannel(ChatLinkChannel channel, String textToSend) {
		return channel.getInterface(ChannelTextInterface.class).send(textToSend).then();
	}
	
	private Mono<Void> sendMessageToChannel(ChatLinkChannel channel, Message message) {
		return
			checkMessageSendAllowedForChannel(channel, message)
			.then(sendMessageToChannelAsQuote(channel, message));
	}

	private Mono<Void> sendMessageToChannelAsQuote(ChatLinkChannel channel, Message message) {
		String textToSend = message.getContent().orElse("<Empty message>");
		return message.getAuthor().map(author->{
			//TODO: Omit header where not needed
			return "**"+author.getHumanReadableID()+"** ("+author.getID().getLong()+")";
		}).flatMap(header->{
			//TODO: Support image
			return sendTextToChannel(channel, header+"\n"+quote(textToSend));
		});
	}
	
	private String quote(String message) {
		return "> "+message
			.replace("\n", "\n> ")
			.replace("@", "@\u200E");
	}
	
	private Mono<Void> checkMessageSendAllowedForGlobal(Message message) {
		return
			checkUserGlobalMuted(message)
			.then(checkImagesSafe(message));
	}
	private Mono<Void> checkUserGlobalMuted(Message message) {
		if (document.getObject().getOrCreateArray("muted").contains(message.getAuthorID())) {
			return Mono.error(new TextException("command.link.error.gmuted"));
		}
		return Mono.empty();
	}
	private Mono<Void> checkImagesSafe(Message message) {
		return Mono.empty();
	}

	private Mono<Void> checkMessageSendAllowedForChannel(ChatLinkChannel channel, Message message) {
		return checkChannelVerified(channel);
	}
	private Mono<Void> checkChannelVerified(ChatLinkChannel channel) {
		return Mono.just(channel.isVerified())
			.flatMap(v->{
				return v?
					Mono.empty():
					Mono.error(new TextException("TODO: Localize error message (We can not access locales in this portion of the code)"));
			});
	}
	
	private Flux<ChatLinkChannel> getChannelFlux() {
		ChatLinkChannel[] channels = channelCache.toArray(new ChatLinkChannel[channelCache.size()]);
		return Flux.fromArray(channels);
	}
	
	private Locale getDefaultLocale() {	
		return botEngine.getLocale(botEngine.getDefaultLocaleName());
		//TODO
	}

	public boolean isAdmin(ChannelID channelID) {
		return document.getObject().getOrCreateObject("data", obj->{}).getOrCreateArray("admins").contains(channelID.getLong());
		//TODO
	}

	public Mono<Void> addAdmin(ChannelID channelID) {
		document.getObject().getOrCreateObject("data", obj->{}).getOrCreateArray("admins").add(channelID.getLong());
		return document.save();
		//TODO
	}
}
