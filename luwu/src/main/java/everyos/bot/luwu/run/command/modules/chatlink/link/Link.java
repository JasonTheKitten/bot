package everyos.bot.luwu.run.command.modules.chatlink.link;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

import everyos.bot.luwu.core.BotEngine;
import everyos.bot.luwu.core.database.DBDocument;
import everyos.bot.luwu.core.entity.ChannelID;
import everyos.bot.luwu.core.entity.User;
import everyos.bot.luwu.core.entity.UserID;
import everyos.bot.luwu.core.exception.TextException;
import everyos.bot.luwu.core.functionality.channel.ChannelTextInterface;
import everyos.bot.luwu.run.command.modules.chatlink.channel.LinkChannel;
import everyos.bot.luwu.run.command.modules.chatlink.user.LinkUser;
import everyos.bot.luwu.run.command.modules.chatlink.util.ModerationUtil;
import everyos.bot.luwu.util.Tuple;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public final class Link {
	
	//TODO: This very much should not be static
	private static final Map<ChannelID, Tuple<UserID, Long>> lastChatTimes = new WeakHashMap<>();
	
	private final DBDocument document;
	private final long id;
	private final List<LinkChannel> channels;

	protected Link(DBDocument document, List<LinkChannel> channels) {
		this.document = document;
		this.id = document.getObject().getOrDefaultLong("clid", -1L);
		this.channels = channels;
	}
	
	public Mono<Void> sendMessage(LinkMessage message) {
		return
			checkGlobalSendAllowed(message)
			.thenMany(getFluxFor(channels))
			.filter(channel -> !channel.getID().equals(message.getOriginChannel().getID()))
			.flatMap(channel -> {
				return
					checkLocalSendAllowed(channel, message)
					.then(sendMessageToChannel(channel, message))
					.onErrorResume(e -> Mono.empty());
			})
			.then();
	}

	public Mono<Void> sendSystemMessage(String message) {
		return getFluxFor(channels)
			.flatMap(channel ->
				sendSystemMessageToChannel(channel, message)
				.onErrorResume(e -> Mono.empty()))
			.then();
	}

	public LinkInfo getInfo() {
		return new LinkInfoImp(document.getObject());
	}

	public Mono<Void> edit(Consumer<LinkEditSpec> func) {
		return Mono.defer(() -> {
			func.accept(new LinkEditSpecImp(document.getObject()));
			return document.save();
		});
	}

	public long getID() {
		return id;
	}

	private Flux<LinkChannel> getFluxFor(List<LinkChannel> channels) {
		return Flux.fromArray(channels.toArray(new LinkChannel[channels.size()]));
	}

	@SuppressWarnings("deprecation")
	private Mono<Void> checkGlobalSendAllowed(LinkMessage message) {
		BotEngine engine = message.getSender().getClient().getBotEngine();
		String key = engine.getConfiguration().getCustomField("moderatecontent-key");
		
		return
			checkUserGlobalMuted(message.getSender())
			.then(checkImagesSafe(message.getAttachments(), key));
	}
	
	private Mono<Void> checkUserGlobalMuted(LinkUser user) {
		return user
			.getInfo()
			.filter(info -> info.isMuted())
			.flatMap(_1 -> Mono.error(new TextException("This text should not appear.")));
	}
	
	private Mono<Void> checkImagesSafe(LinkAttachment[] attachments, String key) {
		return Flux
			.fromArray(attachments)
			.flatMap(attachment -> ModerationUtil.isImageSafe(key, attachment.getURL()))
			.flatMap(b -> {
				if (!b) {
					return Mono.error(new TextException("This text should not appear."));
				}
				
				return Mono.empty();
			})
			.then();
	}
	
	private Mono<Void> checkLocalSendAllowed(LinkChannel channel, LinkMessage message) {
		return
			checkChannelVerified(channel)
			.then(checkUserLocalMuted(channel, message.getSender().getID()));
	}

	private Mono<Void> checkChannelVerified(LinkChannel channel) {
		return channel
			.getInfo()
			.filter(info -> !info.isVerified())
			.flatMap(_1 -> Mono.error(new Exception()));
	}
	
	private Mono<Void> checkUserLocalMuted(LinkChannel channel, UserID senderID) {
		return channel
			.getInfo()
			.filter(info -> info.isUserMuted(senderID))
			.flatMap(_1 -> Mono.error(new Exception()));
	}

	private Mono<Void> sendMessageToChannel(LinkChannel channel, LinkMessage message) {
		String textToSend = addHeaderIfNeeded(quote(message.getContent().orElse("<Empty message>")), channel.getID(), message.getSender());
		
		return channel
			.getInterface(ChannelTextInterface.class)
			.send(spec -> {
				spec.setContent(textToSend);
				for (LinkAttachment attachment: message.getAttachments()) {
					spec.addAttachment(attachment.getName(), attachment.getURL(), attachment.isSpoiler());
				}
			})
			.then();
	}
	
	private String addHeaderIfNeeded(String quote, ChannelID channelID, User author) {
		String textWithHeader = quote;
		if (
			!lastChatTimes.containsKey(channelID) ||
			!lastChatTimes.get(channelID).getT1().equals(author.getID()) ||
			System.currentTimeMillis() - lastChatTimes.get(channelID).getT2() > 120 * 1000) {
			
			textWithHeader = "**" + author.getHumanReadableID() + "** (" + author.getID().getLong() + ")\n" + quote;
		}
		
		lastChatTimes.put(channelID, Tuple.of(author.getID(), System.currentTimeMillis()));
		
		return textWithHeader;		
	}

	private Mono<Void> sendSystemMessageToChannel(LinkChannel channel, String message) {
		String textToSend = "**System** (SYSTEM)\n" + quote(message);
		
		lastChatTimes.remove(channel.getID());
		
		return channel
			.getInterface(ChannelTextInterface.class)
			.send(textToSend)
			.then();
	}
	
	private String quote(String message) {
		return
			"> " +
			message
				.replace("\n", "\n> ")
				.replace("/", "\\/");
	}

	//TODO: This method should not exist
	public static void onChannelReceivedMessage(ChannelID id) {
		lastChatTimes.remove(id);
	}
	
}
