package everyos.bot.luwu.run.command.modules.chatlink.link;

import com.mongodb.client.model.Filters;

import everyos.bot.chat4j.entity.ChatPermission;
import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.Channel;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.Connection;
import everyos.bot.luwu.core.entity.Locale;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.user.LinkUser;
import reactor.core.publisher.Mono;
import xyz.downgoon.snowflake.Snowflake;

public final class LinkUtil {
	
	private LinkUtil() {}
	
	public static Mono<Void> checkPerms(Channel channel, User user, Locale locale) {
		return channel
			.as(LinkChannel.type)
			.flatMap(linkChannel ->
				userOptedByChannel(user, linkChannel)
				.switchIfEmpty(userOptedManually(user, linkChannel))
				.switchIfEmpty(Mono.error(new TextException(locale.localize("command.link.error.needopted"))))
			)
			.then();
	}
	
	private static Mono<Boolean> userOptedManually(User user, LinkChannel channel) {
		return channel
			.getInfo()
			.flatMap(info -> user
				.as(LinkUser.typeWith(info.getLinkID())))
			.flatMap(linkUser -> linkUser.getInfo())
			.filter(info -> info.isOpted())
			.flatMap(_1 -> Mono.just(true));
	}
	
	private static Mono<Boolean> userOptedByChannel(User user, LinkChannel channel) {
		return channel
			.getInfo()
			.filter(info -> info.isOpted())
			.flatMap(_1 -> user.asMemberOf(channel))
			.flatMap(member -> member.getPermissions())
			.map(permissions -> (permissions & ChatPermission.MANAGE_CHANNELS) == ChatPermission.MANAGE_CHANNELS)
			.filter(v -> v);
	}

	public static Mono<Link> getByName(BotEngine engine, String name) {
		return engine
			.getDatabase()
			.collection("chatlinks")
			.scan()
			.filter(Filters.eq("name", name))
			.orEmpty()
			.flatMap(doc -> loadFromDB(engine, doc));
	}

	public static Mono<Link> getByID(BotEngine engine, long id) {
		return engine
			.getDatabase()
			.collection("chatlinks")
			.scan()
			.filter(Filters.eq("clid", id))
			.orEmpty()
			.flatMap(doc -> loadFromDB(engine, doc));
	}
	
	private static Mono<Link> loadFromDB(BotEngine engine, DBDocument document) {
		long linkID = document.getObject().getOrDefaultLong("clid", -1L);
		// Get channels
		return engine
			.getDatabase()
			.collection("channels")
			.scan()
			.with("data.chatlinkid", linkID)
			.with("type", "chatlink")
			.rest()
			.flatMap(channelDocument -> {
				int clientID = channelDocument.getObject().getOrDefaultInt("cliid", 0);
				long channelID = channelDocument.getObject().getOrDefaultLong("cid", -1L);
				Connection connection = engine.getConnectionByID(clientID);
				return new ChannelID(connection, channelID, clientID).getChannel();
			})
			.flatMap(channel->channel.as(LinkChannel.type))
			.onErrorResume(e -> {
				e.printStackTrace();
				return Mono.empty();
			})
			.collectList()
			// Get link
			.map(list -> new Link(document, list));
	}
	
	public static Mono<Link> create(BotEngine engine) {
		long linkID = new Snowflake(0, 0).nextId();
		return engine
			.getDatabase()
			.collection("chatlinks")
			.scan()
				.with("clid", linkID)
			.orCreate(editSpec -> {})
			.flatMap(doc -> doc
				.save()
				.then(loadFromDB(engine, doc)));
	}
	
}
